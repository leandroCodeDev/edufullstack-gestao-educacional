package com.api.edufullstackgestaoeducacional.services.Impl;

import com.api.edufullstackgestaoeducacional.controllers.dtos.requests.RequestTurma;
import com.api.edufullstackgestaoeducacional.controllers.dtos.responses.ResponseTurma;
import com.api.edufullstackgestaoeducacional.entities.CursoEntity;
import com.api.edufullstackgestaoeducacional.entities.DocenteEntity;
import com.api.edufullstackgestaoeducacional.entities.TurmaEntity;
import com.api.edufullstackgestaoeducacional.exception.erros.NotFoundException;
import com.api.edufullstackgestaoeducacional.exception.erros.UnauthorizedException;
import com.api.edufullstackgestaoeducacional.repositories.TurmaRepository;
import com.api.edufullstackgestaoeducacional.services.CursoService;
import com.api.edufullstackgestaoeducacional.services.DocenteService;
import com.api.edufullstackgestaoeducacional.services.TokenService;
import com.api.edufullstackgestaoeducacional.services.TurmaService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class TurmaServiceImpl implements TurmaService {

    private final TurmaRepository repository;

    @Setter
    private CursoService cursoService;
    @Setter
    private DocenteService docenteService;
    @Setter
    private TokenService tokenService;

    public TurmaServiceImpl(TurmaRepository repository) {
        log.info("cria service de turma");
        this.repository = repository;
    }


    @Override
    public ResponseTurma criarTurma(RequestTurma dto, String token) {
        log.info("cria turma");
        String perfil = tokenService.buscaCampo(token, "perfil");
        if (!perfil.equals("PEDAGOGICO") && !perfil.equals("ADMIN")) {
            throw new UnauthorizedException("Acesso não autorizado", "Usuario não tem acesso a essa funcionalidade");
        }
        CursoEntity curso = cursoService.pegaCursoEntity(dto.cursoId()).orElseThrow(() -> new NotFoundException("Curso não encontrado"));
        DocenteEntity docente = docenteService.pegaDocenteEntity(dto.docenteId()).orElseThrow(() -> new NotFoundException("Docente não encontrado"));

        if (!docente.getUsuario().getPerfil().getNome().equals("PROFESSOR")) {
            throw new NotFoundException("O Docente não tem papel de Professor");
        }

        TurmaEntity turma = new TurmaEntity(dto, curso, docente);
        turma = repository.save(turma);
        turma = pegaTurmaEntity(turma.getId()).orElseThrow(() -> new NotFoundException("Turma não encontrado"));
        return turma.toResponseTurma();
    }

    @Override
    public ResponseTurma pegaTurma(Long id, String token) {
        log.info("pega turma");
        String perfil = tokenService.buscaCampo(token, "perfil");
        if (!perfil.equals("PEDAGOGICO") && !perfil.equals("ADMIN")) {
            throw new UnauthorizedException("Acesso não autorizado", "Usuario não tem acesso a essa funcionalidade");
        }
        TurmaEntity turma = pegaTurmaEntity(id).orElseThrow(() -> new NotFoundException("Turma não encontrado"));
        return turma.toResponseTurma();
    }

    @Override
    public Optional<TurmaEntity> pegaTurmaEntity(Long id) {
        log.info("pegar entidade de turma");
        return repository.findById(id);
    }

    @Override
    public ResponseTurma atualizaTurma(long id, RequestTurma dto, String token) {
        log.info("atualiza turma");
        String perfil = tokenService.buscaCampo(token, "perfil");
        if (!perfil.equals("PEDAGOGICO") && !perfil.equals("ADMIN")) {
            throw new UnauthorizedException("Acesso não autorizado", "Usuario não tem acesso a essa funcionalidade");
        }
        TurmaEntity turma = pegaTurmaEntity(id).orElseThrow(() -> new NotFoundException("Turma não encontrado"));
        DocenteEntity docente = docenteService.pegaDocenteEntity(dto.docenteId()).orElseThrow(() -> new NotFoundException("Docente não encontrado"));
        CursoEntity curso = cursoService.pegaCursoEntity(dto.cursoId()).orElseThrow(() -> new NotFoundException("Curso não encontrado"));

        if (!docente.getUsuario().getPerfil().getNome().equals("PROFESSOR")) {
            throw new NotFoundException("O Docente não tem papel de Professor");
        }

        if (!turma.getNome().equals(dto.nome())) {
            turma.setNome(dto.nome());
        }

        if (turma.getCurso().getId() != dto.cursoId()) {
            turma.setCurso(curso);
        }

        if (turma.getDocente().getId() != dto.docenteId()) {
            turma.setDocente(docente);
        }

        repository.save(turma);
        turma = pegaTurmaEntity(id).orElseThrow(() -> new NotFoundException("Turma não encontrado"));

        return turma.toResponseTurma();
    }

    @Override
    public void deleteTurma(Long id) {
        log.info("deleta turma");
        TurmaEntity turma = pegaTurmaEntity(id).orElseThrow(() -> new NotFoundException("Turma não encontrado"));
        repository.delete(turma);
    }

    @Override
    public List<ResponseTurma> pegaTurmas(String token, String s) {
        log.info("pega turmas");
        String perfil = tokenService.buscaCampo(token, "perfil");
        if (!perfil.equals("PEDAGOGICO") && !perfil.equals("ADMIN")) {
            throw new UnauthorizedException("Acesso não autorizado", "Usuario não tem acesso a essa funcionalidade");
        }
        List<TurmaEntity> turmas = repository.findAll();
        if (turmas.size() <= 0) {
            throw new NotFoundException("Não há turmas cadastrados.");
        }
        return turmas.stream()
                .map(TurmaEntity::toResponseTurma)
                .collect(Collectors.toList());
    }

}
