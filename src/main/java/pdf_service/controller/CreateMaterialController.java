package pdf_service.controller;

import com.tutoras.tutoras.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pdf_service.model.CreateMaterialRequest;
import pdf_service.serivce.CreateMaterialService;


@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class CreateMaterialController {
    private final CreateMaterialService createMaterialService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Long> createMaterial(
            @AuthenticationPrincipal
            @RequestBody CreateMaterialRequest request
    ) {

    }
}