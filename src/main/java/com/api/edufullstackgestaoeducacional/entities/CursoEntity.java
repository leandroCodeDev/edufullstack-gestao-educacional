package com.api.edufullstackgestaoeducacional.entities;

import com.api.edufullstackgestaoeducacional.controllers.dtos.requests.RequestCurso;
import com.api.edufullstackgestaoeducacional.controllers.dtos.responses.ResponseCurso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "curso")
@AllArgsConstructor
@NoArgsConstructor
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;


    public CursoEntity(RequestCurso dto) {
        this.nome = dto.nome();
    }


    public ResponseCurso toResponseCurso() {
        return new ResponseCurso(this.id, this.nome);
    }
}
