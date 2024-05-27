package com.challengedesafioalura.literalura.principal;

import com.challengedesafioalura.literalura.Repository.AutorRepository;
import com.challengedesafioalura.literalura.Repository.LibroRepository;
import com.challengedesafioalura.literalura.model.*;
import com.challengedesafioalura.literalura.service.ConsumoAPI;
import com.challengedesafioalura.literalura.service.ConvierteDatos;


import java.util.*;


public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<Libro> librosRegistrados = new ArrayList<>();
    private List<Autor> autoresRegistrados = new ArrayList<>();
    private Datos datos;
    private AutorRepository autorRepositorio;
    private LibroRepository libroRepositorio;


    public Principal(AutorRepository autorRepositorio, LibroRepository libroRepository) {
        this.autorRepositorio = autorRepositorio;
        this.libroRepositorio = libroRepository;
    }



    public void mostrarMenu() {
        int opcion = 1;
        do {
            var menu = """                   
                    
                    ****************************************************************************************************************************
                    ****************************************************************************************************************************
                                            *                                                                      *                                                                                                   
                                            *                                                                      *                            
                                            *                     ELija La opción a través de su número:           *                         
                                            *                     1- buscar libro por título                       * 
                                            *                     2- Listar libros registrados                     * 
                                            *                     3- Listar autores registrados                    *  
                                            *                     4- Listar autores vivos en un determinado año    *
                                            *                     5- Listar libros por idioma                      *                                                                                                        *  
                                            *                     0- salir                                         *
                                            *                                                                      *                                                                       
                    *****************************************************************************************************************************
                    *****************************************************************************************************************************
                                        
                    """;

            try {
                System.out.println(menu);
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroPorTituloWeb();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosEnUnDeterminadoAño();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("""
                                
                                *****************************************************************
                                *****************************************************************
                                                            
                                                 ¡Gracias por usar nuestros servicios!                  
                                                  Cerrando app....................                      
                                                            
                                ****************************************************************
                                ****************************************************************
                                  
                            """);
                        break;
                    default:
                        System.out.println("""
                                           
                         ********************  Opción no validad, elija una opción del menu   *********************
                         
                         """);
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("""
                                           
                                **********************  Opción invalida  ********************
                                                
                                """);
                teclado.nextLine();
            }
        } while (opcion != 0);
    }



    private Datos getDatosLibro() {
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatosAPI(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        datos = conversor.obtenerDatos(json, Datos.class);
        return datos;

    }


    private Libro crearLibro(DatosLibro datosLibro, Autor autor) {
        Libro libro = new Libro(datosLibro, autor);
        return libroRepositorio.save(libro);
    }


    private void buscarLibroPorTituloWeb() {
        System.out.println("¿Qué libro desea buscar?");
        Datos datos = getDatosLibro();
        if (!datos.resultados().isEmpty()) {
            DatosLibro datosLibro = datos.resultados().get(0);
            DatosAutor datosAutor = datosLibro.autor().get(0);
            Libro libro = null;
            Libro libroDb = libroRepositorio.findByTitulo(datosLibro.titulo());
            if (libroDb != null) {
                System.out.println(" " + "\n" +
                        
            "********************* El libro  " +  libroDb.getTitulo().toUpperCase() + " ya existe en la base de datos  *******************"  + "\n" +

             " " );
            } else {
                Autor autorDb = autorRepositorio.findByNombre(datosLibro.autor().get(0).nombre());
                if (autorDb == null) {
                    Autor autor = new Autor(datosAutor);
                    autor = autorRepositorio.save(autor);
                    libro = crearLibro(datosLibro, autor);
                    System.out.println(libro);
                } else {
                    libro = crearLibro(datosLibro, autorDb);
                    System.out.println(libro);
                }
            }
        } else {
            System.out.println("""
                
        ********************** El libro no existe en la base datos web MODB   ***********************
                
        """);
        }
    }


    private void listarLibrosRegistrados() {
        List<Libro> librosRegistrados = libroRepositorio.findAll();
        librosRegistrados.stream()
                .sorted((libro1, libro2) -> libro1.getAutor().getNombre().compareTo(libro2.getAutor().getNombre()))
                .forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        autoresRegistrados = autorRepositorio.findAll();
        autoresRegistrados.stream()
                .sorted((autor1, autor2) -> autor1.getNombre().compareTo(autor2.getNombre()))
                .forEach(System.out::println);
    }


        private void listarAutoresVivosEnUnDeterminadoAño() {
            System.out.println("Ingresa el año que quieres consultar que escritores estaban vivos: ");
            try {
                int yearQuery = teclado.nextInt();
                teclado.nextLine();
                List<Autor> autoresVivos = autorRepositorio.autorVivosEnDeterminadoYear(yearQuery);
                if(autoresVivos.isEmpty()){
                    System.out.println("""
                                    
                     *******************      No hay autores por ese año **********
                                    """);
                }else{
                    autoresVivos.forEach(System.out::println);
                }

            } catch (InputMismatchException e) {
                teclado.nextLine();
                System.out.println("""
                
                    *********************** Invalido, ingrese el año en números  ********************** 
                      
                     """);
            }
        }

   // Lo Deje por si algo, tener otra opción
    /*

        private void listarAutoresVivosEnUnDeterminadoAño() {
        System.out.println("Ingresa el año que quieres consultar que escritores estavan vivos: ");
        try {
            var yearQuery = teclado.nextInt();
            teclado.nextLine();
            List<Autor> consultarAutoresVivoEnUnYear = autoresRegistrados.stream()
                    .filter(a -> {
                        var fechaNacimiento = Integer.parseInt(a.getFechaNacimiento());
                        var fechaMuerte = a.getFechaMuerte() != null ? Integer.parseInt(a.getFechaMuerte()) : Integer.MAX_VALUE;
                        return yearQuery >= fechaNacimiento && (yearQuery < fechaMuerte || fechaMuerte == 0);
                    }).collect(Collectors.toList());
            consultarAutoresVivoEnUnYear.forEach(System.out::println);
        } catch (InputMismatchException e) {
            teclado.nextLine();
            System.out.println("""

        *********************** Invalido, ingrese el año en números  **********************

        """);
        }

    }

     */


    private void listarLibrosPorIdioma() {
        String idioma;
        System.out.println("""
   
   ************************************************************                         
                        Elija un idioma:
                                    
                        1 - Español
                                    
                        2 - Ingles
                        
                        3 - Frances
                        
                        4 - Portugues 
   *************************************************************   
              
                          """);

        var opcion = teclado.nextInt();
        teclado.nextLine();

            if (opcion == 1) {
                idioma = "es";
            } else if (opcion == 2) {
                idioma = "en";
            } else if (opcion == 3) {
                idioma = "fr";
            } else if (opcion == 4) {
                idioma = "pt";
            }else {
                idioma = null;
                System.out.println("""
                      
                      *************************  Opción no válida  ************************
            """);
            }

            List<Libro> librosPorIdioma = libroRepositorio.findByIdiomasContaining(idioma);
            if (librosPorIdioma.isEmpty()) {
                System.out.println("""
            
            ************ No se encontraron libros en el idioma seleccionado  **************** 
            
            """);
            } else {
              var  cantidadLibrosPorIdioma =libroRepositorio.countByLanguage(idioma);
                System.out.println( " " + "\n" +
                      
                      "**************** Hay " + cantidadLibrosPorIdioma + " libros de este idioma ******************" + "\n" +

                        "  ");
                  librosPorIdioma.forEach(System.out::println);
              }

      }



  }
