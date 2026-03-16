package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.request.DealerInvoiceMailRequest;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    @SneakyThrows
    public void sendDealerInvoiceInstruction(String to, DealerInvoiceMailRequest request) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom("tracksys@hedeffilo.com");
        helper.setTo(to);
        helper.setSubject("Fatura Bilgilendirmesi - İş Emri No: " + request.getOrdersId());
        helper.setText(buildMailBody(request), true);
        try {
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.warn("Mail gönderilemedi, devam ediliyor: {}", e.getMessage());
        }
        log.info("Bayi mail gönderildi. OrdersId: {}, To: {}", request.getOrdersId(), to);
    }

    private String buildMailBody(DealerInvoiceMailRequest r) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; font-size: 14px;">
                    <p>Merhaba,</p>
                    <p>Fatura işlemlerine başlayabilirsiniz.
                       Faturalar kesilirken aşağıdaki notları dikkate almanızı rica ederim.</p>
                    <ul>
                        <li>
                            <strong>E-fatura sistemi hakkında;</strong> fatura keserken açıklama kısmına
                            7 haneli iş emri no.yu yazmanızı<br/>
                            <strong>(BUNUN DOĞRU YAZILMASI ÇOK ÖNEMLİ; KARAKTER BOŞLUKLARINA DİKKAT ETMENİZİ RİCA EDERİM.</strong>
                            &nbsp; Örneğin <strong style="background-color: yellow;">İş Emri No: %s</strong>)
                        </li>
                        <li>Fatura türünün <strong>"TİCARİ"</strong> olarak düzenlenmesini rica ederiz</li>
                    </ul>
                    <table border="1" cellpadding="6" cellspacing="0"
                           style="border-collapse: collapse; margin-top: 16px;">
                        <thead style="background-color: #f2f2f2;">
                            <tr>
                                <th style="color: #c00000;">İş Emri No</th>
                                <th style="color: #c00000;">Vehicle description</th>
                                <th style="color: #c00000;">Customer tradingName</th>
                                <th style="color: #c00000;">Supplier tradingName</th>
                                <th style="color: #c00000;">Renk</th>
                                <th style="color: #c00000;">Teslimat</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                            </tr>
                        </tbody>
                    </table>
                    <br/>
                    <p>Saygılarımla</p>
                </body>
                </html>
                """.formatted(
                r.getOrdersId(),
                r.getOrdersId(),
                r.getVehicleDescription(),
                r.getCustomerTradingName(),
                r.getSupplierTradingName(),
                r.getColor(),
                r.getDeliveryLocation()
        );
    }
}