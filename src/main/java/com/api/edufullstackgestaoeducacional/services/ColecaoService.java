package com.api.edufullstackgestaoeducacional.services;

public interface ColecaoService {

    PerfilService getPerfilService();

    UsuarioService getUsuarioService();

    TokenService getTokenService();

    SenhaService getSenhaService();

    DocenteService getDocenteService();

    CursoService getCursoService();

    MateriaService getMateriaService();

    TurmaService getTurmaService();

    AlunoService getAlunoService();

    NotaService getNotaService();
}
