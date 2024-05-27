package com.challengedesafioalura.literalura.Repository;

import com.challengedesafioalura.literalura.model.Autor;
import com.challengedesafioalura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Libro findByTitulo(String titulo);


    List<Libro> findByIdiomasContaining(String idioma);

    @Query("SELECT COUNT(l) FROM Libro l WHERE l.idiomas = :idiomas")
    int countByLanguage(@Param("idiomas") String idiomas);


}
