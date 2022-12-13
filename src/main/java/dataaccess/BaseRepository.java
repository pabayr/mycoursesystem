package dataaccess;

import java.util.List;
import java.util.Optional; // Optional->Eine Art Wrapperklasse
//ALLE erbenden Klassen müssen Typinformationen zu T & I bereitstellen
// T und I sind generische Datentypen, sprich alle möglichen Referenztypen

// TLDR:
// Parametrisierte Typen haben den Vorteil, dass man weitere Repositories, die z.B. nicht kursspezifisch sind, anlegen kann (Studenten,Lehrer....)

public interface BaseRepository<T,I> {

//Basic CRUD-Methoden

    Optional<T> insert(T entity);
    Optional<T> getById(I id);
    List<T> getAll();
    Optional<T> update(T entity);
    void deleteById(I id);

}
