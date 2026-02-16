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

            // SENİN HAZIRLADIĞIN PROMPT
            String promptKurallar = """
                Sen profesyonel bir Türk Araç Ruhsatı (Tescil Belgesi) ayrıştırma uzmanısın.
                Sana bozuk, silik veya kaymış OCR metinleri verilecek. Görevin bu metni analiz edip aşağıdaki kurallara göre temiz bir JSON oluşturmaktır.
            
                KURALLAR:
                Özellikle dikkat et: O ≠ 0 , I ≠ 1 ,  Z ≠ 2 , S ≠ 5 , B ≠ 8
                
                1. TARİH FORMATI: Tarih içeren tüm alanları mutlaka 'DD/MM/YYYY' formatına çevir.
                2. STANDARTLAŞTIRMA: Plaka boşluksuz olsun (34ABC123 -> 34 ABC 123), Markaları düzelt.
                3. KAYIP VERİ: Okuyamıyorsan 'Bilinmiyor' yaz.
                4. MOTOR NO: Genelde 'Motor No' ibaresinin yanındadır, dikkatli bul.
                5. BELGE SERİ NO: FO No kısmını alma, sadece No-123456 formatında ver.
                6. TEMİZLİK: Sadece saf JSON ver. Markdown kullanma.
                
                İSTENEN JSON FORMATI:
                {
                  "VerildigiIlIlce": "...", "Plaka": "...", "IlkTescilTarihi": "...", "TescilSiraNo": "...",
                  "TescilTarihi": "...", "Markasi": "...", "Tipi": "...", "TicariAdi": "...", "ModelYili": "...",
                  "AracSinifi": "...", "Cinsi": "...", "Rengi": "...", "MotorNo": "...", "SaseNo": "...",
                  "NetAgirligi": "...", "AzamiYukluAgirligi": "...", "KoltukSayisi": "...", "SilindirHacmi": "...",
                  "MotorGucu": "...", "YakitCinsi": "...", "KullanimAmaci": "...", "TipOnayNo": "...",
                  "TCKimlikVergiNo": "...", "SahibiAdSoyadUnvan": "...", "Adres": "...", "NoterSatisTarihi": "...",
                  "DigerBilgiler": "...", "BelgeSeriNo": "..."
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