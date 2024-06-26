package fun.ewjx.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {
    private String fileName;
    private byte[] fileData;
}