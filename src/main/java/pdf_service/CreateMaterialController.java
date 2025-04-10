package pdf_service;

import com.tutoras.tutoras.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


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

        if (!materialService.areMaterialsAccessible(request.getMaterialIds())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Some materials are not accessible");
        }
        Long materialId = materialService.createMaterial(authenticatedUser, request);
        return ResponseEntity.ok(materialId);
    }

}