package pdf_service.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@Builder
@AllArgsConstructor
public class CreateMaterialRequest {
    ArrayList<Long> sources_id;
}