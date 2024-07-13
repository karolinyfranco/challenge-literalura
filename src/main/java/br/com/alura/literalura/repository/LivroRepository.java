package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    Boolean existsByTitulo(String titulo);
    List<Livro> findTop10ByOrderByNumeroDeDownloadsDesc();

    @Query("SELECT l FROM Livro l WHERE l.idioma = :idiomaSelecionado")
    List<Livro> buscarIdiomas(String idiomaSelecionado);
}
