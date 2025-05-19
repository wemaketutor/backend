package pdf_service.controller;

import com.tutoras.tutoras.security.UserPrincipal;
import com.tutoras.tutoras.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pdf_service.model.CreateMaterialRequest;
import pdf_service.serivce.CreateMaterialService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/materials/generate-pdf")
@RequiredArgsConstructor
public class CreateMaterialController {
    private final CreateMaterialService createMaterialService;
    private final AuthService authService;

    @PostMapping
    public CompletableFuture<ResponseEntity<?>> createMaterial(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateMaterialRequest request
    ) {
        // Проверка аутентификации
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        var roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (!roles.contains("ROLE_TEACHER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can create materials");
        }

        if (request == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid request data");
        }

        try {
            Long materialId = createMaterialService.createMaterial(principal.getUserId(), request);
            return ResponseEntity.ok(materialId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate PDF");
        }
    }
}