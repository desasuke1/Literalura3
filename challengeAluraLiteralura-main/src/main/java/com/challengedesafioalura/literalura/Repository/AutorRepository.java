package com.challengedesafioalura.literalura.Repository;

import com.challengedesafioalura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Autor findByNombre(String nombre);


    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :year AND :year < a.fechaMuerte OR a.fechaMuerte = 0 ")
    List<Autor> autorVivosEnDeterminadoYear(@Param("year") int year);

}
