package cj.demo.aesusage.controller;

import cj.demo.aesusage.model.ConfidentialPackage;
import cj.demo.aesusage.utils.EncryptUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/private")
public class ConfidentialController {

    @Value( "${aes.key}" )
    private String aesKey;

    @GetMapping("/greeting")
    public ConfidentialPackage greeting(@RequestParam(value = "name", defaultValue = "World") String name,
                                        @RequestParam(value = "cipherMode", defaultValue = "CBC") String cipherMode) throws Exception {
        System.out.println("AES Cipher Mode:" + cipherMode);
        System.out.println(name);
        name = URLDecoder.decode(name, StandardCharsets.UTF_8);
        System.out.println(name);

        switch (cipherMode) {
            case "CBC" -> name = EncryptUtils.decryptAESCBC(name, this.aesKey);
            case "GCM" -> {
                try {
                    name = EncryptUtils.decryptAESGCM(name, this.aesKey);
                } catch (javax.crypto.AEADBadTagException e) {
                    System.out.println("Possibly tampered message!");
                    System.out.println("Returning same content.");
                    System.out.println("Exception:" + e);
                }
            }
            default -> name = EncryptUtils.decryptAESCBC(name, this.aesKey);
        }

        System.out.println("Decrypted:" + name);

        return new ConfidentialPackage(name);
    }

}
