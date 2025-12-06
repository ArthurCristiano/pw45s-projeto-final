package br.edu.utfpr.pb.pw44s.server.dto;

import br.edu.utfpr.pb.pw44s.server.model.OrderAttachment;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AttachmentResponseDTO {
    private Long id;
    private String fileName;
    private String fileType;

    public AttachmentResponseDTO(OrderAttachment entity) {
        this.id = entity.getId();
        this.fileName = entity.getFileName();
        this.fileType = entity.getFileType();
    }
}