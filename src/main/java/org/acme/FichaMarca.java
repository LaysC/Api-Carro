package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class FichaMarca extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    public Long id;

    @OneToOne(mappedBy = "perfil")
    @JsonIgnore
    public Marca marca;

    @NotBlank(message = "A história da marca não pode ser vazia")
    public String historia;

    public String fundadores;

    public String premiosConquistados;

    public FichaMarca() {
    }
}
