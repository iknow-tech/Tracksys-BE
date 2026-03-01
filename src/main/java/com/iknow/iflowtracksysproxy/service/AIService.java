package com.iknow.iflowtracksysproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AIService {

    @Value("${azure.openai.endpoint}")
    private String azureEndpoint;

    @Value("${azure.openai.key}")
    private String azureKey;

    @Value("${azure.openai.deployment}")
    private String deploymentName;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String parseRuhsatInfo(String ocrMetni) {
        log.info("AI Ruhsat Analizi Başlıyor...");

        try {
            // URL Temizleme ve Hazırlama Mantığın
            String temizEndpoint = azureEndpoint
                    .replace("/openai/v1/", "")
                    .replace("/openai/v1", "");

            if (temizEndpoint.endsWith("/")) {
                temizEndpoint = temizEndpoint.substring(0, temizEndpoint.length() - 1);
            }

            String url = temizEndpoint + "/openai/deployments/" + deploymentName
                    + "/chat/completions?api-version=2024-08-01-preview";

            // JSON Gövdesini Hazırla
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode messagesNode = rootNode.putArray("messages");

            // PROMPT
            String promptKurallar = """
                Sen profesyonel bir Türk Araç Ruhsatı (Tescil Belgesi) ayrıştırma uzmanısın.
                Sana bozuk, silik veya kaymış OCR metinleri verilecek. Görevin bu metni analiz edip aşağıdaki kurallara göre temiz bir JSON oluşturmaktır.
            
                KURALLAR:
                Özellikle dikkat et: O ≠ 0 , I ≠ 1 ,  Z ≠ 2 , S ≠ 5 , B ≠ 8
                
                1. TARİH FORMATI: Tarih içeren tüm alanları mutlaka 'DD/MM/YYYY' formatına çevir.(Örn: '12.01 2023' -> '12/01/2023', '2023-01-12' -> '12/01/2023').
                2. STANDARTLAŞTIRMA: Plaka boşluksuz olsun (34ABC123 -> 34 ABC 123), Markaları düzelt. Eğer aşağıdaki alanlarda metni tam okuyamıyorsan ve metinde aşağıda yazdıklarıma benzer ifadeler varsa, yazan metni buna benzeterek düzelt:
                    - Yakıt Cinsi: [BENZİN, DİZEL, LPG, ELEKTRİK, HİBRİT, BENZİN/LPG, DİZEL/HİBRİT]
                    - Vites/Tip: [MANUEL, OTOMATİK, YARI OTOMATİK]
                    - Kullanım Amacı: [HUSUSİ, TİCARİ, YOLCU NAKLİ, YÜK NAKLİ]
                3. KAYIP VERİ: Eğer bir veriyi metinde kesinlikle bulamazsan, okuyamıyorsan değerine 'Bilinmiyor' yaz. (Boş bırakma). Yanlış tahmin etme, açıklama ekleme.
                4. MOTOR NO: Motor No genellikle 'Motor No', 'Motor Numarası', 'Motor No:', 'Motor No -', 'Motor No .' gibi etiketlerle belirtilir. Motor numaraları standart değildir. Kısa (5-6 hane) veya uzun olabilir. - Genelde "Motor No", "Motor", "M.No" ibarelerinin yanında veya altındadır. Sadece sayı veya sayı+harf kombinasyonu olabilir.
                5. BELGE SERİ NO: Belge üzerindeki 2 harf ve 6 rakamdan oluşan diziyi bul. Eğer "FO No 123456" veya "IF No 123456" gibi ibareler varsa, "No" ve boşlukları atarak sadece "FO123456" veya "IF123456" formatında birleştir. Çıktı her zaman [2 Harf][6 Rakam] bitişik olmalıdır.
                6. TEMİZLİK: Çıktıda JSON dışında hiçbir 'Merhaba', 'İşte sonuç' veya '```json' etiketi kullanma. Sadece saf JSON ver.
                7. DİĞER BİLGİLER: Eğer "Diğer Bilgiler" kısmında Eğer "mua.geç.trh", "mua. geç. trh", "muayene geçerlilik", "muayene geç. trh" gibi bir ifade varsa bu alanı "IlkMuayeneGecerlilikTarihi" alanına yaz ve ve "Diğer Bilgiler" alanını boş bırak. Eğer muayene tarihi dışında başka ibareler varsa (rehinlidir, engelli aracı, LPG'li vb.) bunları "DigerBilgiler" alanına yaz.- Muayene Tarihi: 'Mum. Geç. Trh', 'M.G.T', 'Muayene' geçen tarihleri yakala. (Örn: "Muayene Geçerlilik: 10/10/2025"). Bunun dışında:
                    - Trafik Durumu: 'Trafikten Çekme', 'Hurdaya Ayrılma' veya 'Men' ibareleri ve varsa tarihlerini yakala. Diğer Bilgiler içerisine yaz.
                    - Hak Mahrumiyeti: 'Rehinlidir', 'Hacizlidir', 'Satılamaz' gibi şerhleri yakala.
                    - LPG/Tadilat: Varsa tadilat tarihlerini yakala.
                     *Eğer bu bilgilerden birden fazlası varsa aralarına virgül koyarak tek bir string olarak yaz. Bunlardan başka bir bilgi varsa stringi oku.*
                8. PLAKA: Plakada genel format [İl Kodu] + [Harf Grubu] + [Rakam Grubu] kombinasyonundan oluşuyor. Sayı mı harf mi olduğunu algılayamazsan bu formata dikkat et. Okuduğun plakayı aralarında boşluk olmayacak şekilde yaz. Örneğin "11AA111", "22A333" gibi.
                9. ÖNCELİK: Bizim için en kritik çıktılar: Plaka, Tescil Tarihi, Belge Seri No ve Şasi No'dur. Bunları bulmak için ekstra çaba sarf et.

                İSTENEN JSON FORMATI:
                {
                  "VerildigiIlIlce": "...", "Plaka": "...", "IlkTescilTarihi": "...", "TescilSiraNo": "...",
                  "TescilTarihi": "...", "Markasi": "...", "Tipi": "...", "TicariAdi": "...", "ModelYili": "...",
                  "AracSinifi": "...", "Cinsi": "...", "Rengi": "...", "MotorNo": "...", "SaseNo": "...",
                  "NetAgirligi": "...", "AzamiYukluAgirligi": "...", "KoltukSayisi": "...", "SilindirHacmi": "...",
                  "MotorGucu": "...", "YakitCinsi": "...", "KullanimAmaci": "...", "TipOnayNo": "...",
                  "TCKimlikVergiNo": "...", "SahibiAdSoyadUnvan": "...", "Adres": "...", "NoterSatisTarihi": "...",
                    "IlkMuayeneGecerlilikTarihi": "...", "DigerBilgiler": "...", "BelgeSeriNo": "..."
                }
                """;

            // Mesajları Ekle
            ObjectNode systemMsg = mapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", promptKurallar);
            messagesNode.add(systemMsg);

            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", "İşte analiz edilecek OCR metni:\n" + ocrMetni);
            messagesNode.add(userMsg);

            rootNode.put("temperature", 0.0); // Kararlılık için 0.0 önerilir

            String jsonBody = mapper.writeValueAsString(rootNode);

            // İsteği Gönder
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("api-key", azureKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseRoot = mapper.readTree(response.body());
                String content = responseRoot.path("choices").get(0).path("message").path("content").asText();

                log.info("AI Analizi Tamamlandı.");
                return content.replace("```json", "").replace("```", "").trim();
            } else {
                log.error("Azure OpenAI Hatası: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("AI Servis Hatası: " + response.statusCode());
            }

        } catch (Exception e) {
            log.error("AI İşlem Hatası: {}", e.getMessage());
            throw new RuntimeException("AI Analizi sırasında hata oluştu.", e);
        }
    }
}