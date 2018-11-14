package b2w.desafio.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "planetas")
public class Planeta {

    @Id
    String id;

    @Indexed(unique = true)
    @NotBlank(message="Nome não definido")
    @Size(min=3, message="O nome deve ter pelo menos 3 caracteres")
    String nome;

    @NotBlank(message="Clima não definido")
    @Size(min=3, message="O clima deve ter pelo menos 3 caracteres")
    String clima;

    @NotBlank(message="Terreno não definido")
    @Size(min=3, message="O terreno deve ter pelo menos 3 caracteres")
    String terreno;

    @NotNull(message="A quantidade de aparição em filmes não definida")
    @Min(0)
    Integer filmes;

}
