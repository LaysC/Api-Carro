package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class Marca extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    public Long id;

    @NotBlank(message = "O nome da marca não pode ser vazio")
    @Size(min = 2, max = 100, message = "O nome da marca deve ter entre 2 e 100 caracteres")
    public String nomeDaMarca;

    public String nomeCompletoEmpresa;

    @Past(message = "A data de fundação deve ser no passado")
    public LocalDate dataDeFundacao;

    @NotBlank(message = "O país de origem é obrigatório")
    @Size(max = 80)
    public String paisDeOrigem;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_marca_id")
    public FichaMarca perfil;

    @OneToMany(mappedBy = "marca", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Carro> carros = new ArrayList<>();

    public Marca() {}

    public Marca(Long id, String nomeDaMarca, String nomeCompletoEmpresa, LocalDate dataDeFundacao, String paisDeOrigem, FichaMarca perfil) {
        this.id = id;
        this.nomeDaMarca = nomeDaMarca;
        this.nomeCompletoEmpresa = nomeCompletoEmpresa;
        this.dataDeFundacao = dataDeFundacao;
        this.paisDeOrigem = paisDeOrigem;
        this.perfil = perfil;
    }
}
