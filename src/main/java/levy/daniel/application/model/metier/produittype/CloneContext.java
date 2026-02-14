package levy.daniel.application.model.metier.produittype;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * <style>p, ul, li {line-height : 1em;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">CLASSE CloneContext :</p>
 * <p>Classe utilitaire pour gérer le clonage profond des objets
 * en évitant les cycles de référence.</p>
 * <p>Utilise une <code>IdentityHashMap</code> 
 * synchronisée pour stocker les objets
 * déjà clonés et éviter les doublons.</p>
 * <p>Permet de garantir que chaque objet n'est cloné qu'une seule fois,
 * même s'il est référencé plusieurs fois dans la hiérarchie d'objets.</p>
 * <p style="font-weight:bold;">Thread-safe :</p>
 * <p>La map est synchronisée pour garantir 
 * la sécurité en environnement multi-thread.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.1
 * @since 13 janvier 2026
 */
public final class CloneContext {

    // ************************ATTRIBUTS************************************/

    
    /**
     * <div>
     * <p>IdentityHashMap jouant un rôle de cache pour les clones.</p>
     * <p>Thread-safety : Utilisation de Collections.synchronizedMap
     * pour encapsuler l’IdentityHashMap,
     * ce qui garantit un accès thread-safe tout en conservant
     * la sémantique d’identité des objets.</p>
     * </div>
     */
    private final Map<Object, Object> cache
        = Collections.synchronizedMap(new IdentityHashMap<>());

    
    /**
     * <style>p, ul, li {line-height : 1em;}</style>
     * <div>
     * <p>LOG : Logger :</p>
     * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
     * <p>dépendances :</p>
     * <ul>
     * <li><code>org.apache.logging.log4j.Logger</code></li>
     * <li><code>org.apache.logging.log4j.LogManager</code></li>
     * </ul>
     * </div>
     */
    private static final Logger LOG = LogManager
            .getLogger(CloneContext.class);

    // *************************METHODES************************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public CloneContext() {
        super();
    } // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________

    
    
    /**
     * <div>
     * <p>Retourne le clone déjà créé pour key
     * (cache IdentityHashMap), ou null si absent.</p>
     * </div>
     *
     * @param <T> Type générique du clone.
     * @param key : Object Clé de l'objet original.
     * @return T Clone de l'objet original, ou null si absent.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Object key) {
        return (T) cache.get(key);
    }

    
    
    /**
     * <div>
     * <p>Enregistre l’association original → clone dans le cache
     * , pour garantir l'unicité et casser les cycles.</p>
     * </div>
     *
     * @param key : Object Clé de l'objet original.
     * @param value : Object Clone de l'objet original.
     */
    public void put(final Object key, final Object value) {
        cache.put(key, value);
    }

    
    
    /**
     * <div>
     * <p>Vérifie si un objet original a déjà été cloné.</p>
     * </div>
     *
     * @param key : Object Clé de l'objet original.
     * @return boolean true si l'objet a déjà été cloné, false sinon.
     */
    public boolean contains(final Object key) {
        return cache.containsKey(key);
    }

    
    
    /**
     * <div>
     * <p>Retourne le nombre d'objets déjà clonés.</p>
     * </div>
     *
     * @return int Nombre d'objets déjà clonés.
     */
    public int size() {
        return cache.size();
    }

    
    
    /**
     * <div>
     * <p>Vide le cache des objets clonés.</p>
     * </div>
     */
    public void clear() {
        cache.clear();
    }
}
