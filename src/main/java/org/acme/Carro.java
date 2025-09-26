package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Carro extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "O modelo não pode ser vazio")
    @Size(min = 1, max = 200)
    public String modelo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 2000)
    public String descricao;

    @Min(value = 1900, message = "Ano de fabricação inválido")
    public int anoFabricacao;

    @DecimalMin(value = "0.0", inclusive = true, message = "Avaliação mínima é 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Avaliação máxima é 10.0")
    public double avaliacao;

    @Min(value = 0, message = "Cilindradas não podem ser negativas")
    public int cilindradas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marca_id")
    public Marca marca;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "carro_acessorio",
            joinColumns = @JoinColumn(name = "carro_id"),
            inverseJoinColumns = @JoinColumn(name = "acessorio_id")
    )
    public Set<Acessorio> acessorios = new HashSet<>();

    public Carro() {}

    public Carro(Long id, String modelo, String descricao, int anoFabricacao, double avaliacao, int cilindradas) {
        this.id = id;
        this.modelo = modelo;
        this.descricao = descricao;
        this.anoFabricacao = anoFabricacao;
        this.avaliacao = avaliacao;
        this.cilindradas = cilindradas;
    }
}
