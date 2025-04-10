package pdf_service;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@Builder
@AllArgsConstructor
public class CreateMaterialRequest {
    ArrayList<Long> materials_id;
}