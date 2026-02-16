package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduitI;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">CLASSE ConvertisseurMetierToJPA.java :</p>
 *
 * <p>
 * Cette classe final est une classe UTILITAIRE chargée de
 * <span style="font-weight:bold;">convertir des objets métier</span>
 * en <span style="font-weight:bold;">Entity JPA</span>
 * </p>
 *
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemple d'utilisation : </p>
 * <ul>
 * <li><code>// retourne un TypeProduitJPA après
 * conversion d'un TypeProduit</code>.</li>
 * <li><code>final TypeProduitJPA typeProduitPecheJPA
 * = ConvertisseurMetierToJPA.typeProduitToJPA(
 * new TypeProduit("Pêche"));</code></li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 30 déc. 2025
 */
public final class ConvertisseurMetierToJPA {

/* =========================== CONSTANTES ===============================*/

    /**
     * System.getProperty("line.separator")
     */
    public static final String SAUT_DE_LIGNE
        = System.getProperty("line.separator");

    /**
     * "unchecked"
     */
    public static final String UNCHECKED = "unchecked";

    /**
     * "null"
     */
    public static final String NULL = "null";

    /**
     * "Implémentation non-METIER "
     */
    public static final String IMPLEMENTATION_NON_METIER
        = "Implémentation non-METIER ";

    /**
     * "[idTypeProduit : "
     */
    public static final String IDTYPEPRODUIT = "[idTypeProduit : ";

    /**
     * "%-2s"
     */
    public static final String FORMAT_ID = "%-2s";

    /**
     * "%-3s"
     */
    public static final String FORMAT_IDPRODUIT = "%-3s";

    /**
     * "%-3s"
     */
    public static final String FORMAT_IDSTP = "%-3s";

    /**
     * "%-3s"
     */
    public static final String FORMAT_IDTP = "%-3s";

    /**
     * "%-20s"
     */
    public static final String FORMAT_STP = "%-20s";

    /**
     * " - sousTypeProduit : "
     */
    public static final String SOUS_TYPE_PRODUIT
    	= " - sousTypeProduit : ";

    /**
     * "%-13s"
     */
    public static final String FORMAT_TP = "%-13s";

    /**
     * "%-40s"
     */
    public static final String FORMAT_P = "%-40s";

    /**
     * " - "
     */
    public static final String TIRET_ESPACE = " - ";

    /**
     * ']'
     */
    public static final char CROCHET_FERMANT = ']';

    /**
     * " : "
     */
    public static final String DEUX_POINTS_ESPACE = " : ";
 
    
    // ************************ATTRIBUTS**********************************/
    
    /**
     * <div>
     * <p>Cache statique thread-safe pour les instances converties.</p>
     * </div>
     */
    private static final Map<Object, Object> SHARED_CACHE =
        Collections.synchronizedMap(new IdentityHashMap<>());

    /* ----------------------------------------------------------------- */

    /**
     * <style>p, ul, li {line-height : 1em;}</style>
     * <div>
     * <p>LOG : Logger : </p>
     * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
     * <p>dépendances : </p>
     * <ul>
     * <li><code>org.apache.logging.log4j.Logger</code></li>
     * <li><code>org.apache.logging.log4j.LogManager</code></li>
     * </ul>
     * </div>
     */
    private static final Logger LOG = LogManager
            .getLogger(ConvertisseurMetierToJPA.class);

    // *************************METHODES**********************************/

    /**
    * <div>
    * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
    * <p>Constructeur private pour bloquer l'instanciation de la classe.</p>
    * </div>
    */
    private ConvertisseurMetierToJPA() {
        super();
    } // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________

    
    
    /* ============================================================ */
    /* CONTEXTE DE CONVERSION (cycle-safe)                           */
    /* ============================================================ */
    /**
     * <div>
     * <p style="font-weight:bold;">
     * gère un cache (IdentityHashMap) pour les convertisseurs
     * </p>
     * </div>
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 14 janvier 2026
     */
    private static final class ConversionContext {

        // ************************ATTRIBUTS*****************************/

        /**
         * <div>
         * <p>IdentityHashMap servant de cache</p>
         * </div>
         */
        private final Map<Object, Object> cache;

        // *********************** METHODES *****************************/

        
        
        /**
         * <div>
         * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
         * </div>
         */
        private ConversionContext() {
            this.cache = new IdentityHashMap<>();
        }

        
        
        /**
         * <div>
         * <p style="font-weight:bold;">
         * retourne l'objet mis en cache à la clé pKey.</p>
         * <ul>
         * <li>Essaie d'abord de retourner l'objet dans le cache partagé
         * Thread-Safe static de la classe englobante
         * {@code ConvertisseurMetierToJPA} {@link SHARED_CACHE}</li>
         * <li>Sinon, retourne l'objet dans le cache private local
         * {@code this.cache} de la classe interne static
         * {@code ConversionContext}.</li>
         * </ul>
         * </div>
         *
         * @param <T> : T :
         * Objet retourné par le cache.
         * @param pKey : Object :
         * clé d'identification.
         * @return T :
         * Type de l'objet.
         */
        @SuppressWarnings(UNCHECKED)
        private <T> T get(final Object pKey) {

            /* Essaie d'abord de retourner l'objet
             * dans le cache partagé Thread-Safe static
             * de la classe englobante ConvertisseurMetierToJPA :
             * SHARED_CACHE. */
            if (SHARED_CACHE.containsKey(pKey)) {
                return (T) SHARED_CACHE.get(pKey);
            }

            /* Sinon, retourne l'objet dans le cache private local
             * this.cache
             * de la classe interne static ConversionContext. */
            return (T) this.cache.get(pKey);
        }

        
        
        /**
         * <div>
         * <p style="font-weight:bold;">
         * met en cache un Object value à la clé pKey.</p>
         * <ul>
         * <li>Alimente le cache partagé Thread-Safe static
         * de la classe englobante
         * {@code ConvertisseurMetierToJPA} {@link SHARED_CACHE}.</li>
         * <li>Alimente le cache private local interne {@code this.cache}
         * de la classe interne static {@code ConversionContext}.</li>
         * </ul>
         * </div>
         *
         * @param pKey : Object : clé dans le cache
         * @param pValue : Object : valeur stockée avec pKey
         */
        private void put(final Object pKey, final Object pValue) {

            /* Alimente le cache Thread-Safe partagé static SHARED_CACHE. */
            SHARED_CACHE.put(pKey, pValue);

            /* Alimente le cache private local interne this.cache
             * de la classe interne static ConversionContext. */
            this.cache.put(pKey, pValue);
        }
    }

    
    
    /* ============================================================ */
    /* API PUBLIQUE                                                  */
    /* ============================================================ */

    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme un objet métier <code style="font-weight:bold;">
     * TypeProduit</code>
     * en Entity JPA <code style="font-weight:bold;">
     * TypeProduitJPA</code>.</p>
     * </div>
     *
     * @param pTypeProduit : TypeProduit :
     * Objet métier correspondant à l'Entity JPA TypeProduitJPA.
     * @return TypeProduitJPA : Entity JPA associé à l'objet métier.
     */
    public static TypeProduitJPA typeProduitMETIERToJPA(
            final TypeProduit pTypeProduit) {
        return typeProduitMETIERToJPA(
                pTypeProduit, new ConversionContext());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme un objet métier 
     * <code style="font-weight:bold;">SousTypeProduit</code>
     * en Entity JPA 
     * <code style="font-weight:bold;">SousTypeProduitJPA</code>.</p>
     * </div>
     *
     * @param pSousTypeProduit : SousTypeProduit :
     * Objet métier correspondant à l'Entity JPA SousTypeProduitJPA.
     * @return SousTypeProduitJPA : Entity JPA associé à l'objet métier.
     */
    public static SousTypeProduitJPA sousTypeProduitMETIERToJPA(
            final SousTypeProduit pSousTypeProduit) {
        return sousTypeProduitMETIERToJPA(
                pSousTypeProduit, new ConversionContext());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme un objet métier 
     * <code style="font-weight:bold;">Produit</code>
     * en Entity JPA 
     * <code style="font-weight:bold;">ProduitJPA</code>.</p>
     * </div>
     *
     * @param pProduit : ProduitI :
     * Objet métier correspondant à l'Entity JPA ProduitJPA.
     * @return ProduitJPA : Entity JPA associé à l'objet métier.
     */
    public static ProduitJPA produitMETIERToJPA(
            final Produit pProduit) {
        return produitMETIERToJPA(pProduit, new ConversionContext());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme un objet métier <code style="font-weight:bold;">
     * TypeProduit</code>
     * en Entity JPA <code style="font-weight:bold;">
     * TypeProduitJPA</code>.</p>
     * <ul>
     * <li>ne fait rien et retourne null si pTypeProduit == null.</li>
     * <li>ne fait rien et retourne null si
     * pTypeProduit.getTypeProduit() est blank (null ou espaces).</li>
     * <li>retourne l'Entity JPA s'il était déjà dans le cache.</li>
     * <li>instancie un nouveau TypeProduitJPA.</li>
     * <li>met le nouveau TypeProduitJPA dans le cache.</li>
     * <li>passe les paramètres scalaires au nouveau TypeProduitJPA.</li>
     * <li>CONVERTIT et ALIMENTE la liste ENFANT sousTypeProduits</li>
     * <li>passe la nouvelle liste des sousTypeProduits convertis au
     * nouveau TypeProduitJPA.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
     * Diagramme de Classes du Produit qualifié par un SousTypeProduit qui est lui-même une déclinaison d'un TypeProduit</p>
     * <p>
     * <img src="../../../../../../../../../../../javadoc/images/persistence/metier/produittype/entities/entitiesJPA/diagramme_classes_produittype.jpg"
     * alt="diagramme de classes d'un Produit Typé" border="1" align="center" height= 200px />
     * </p>
     * </div>
     *
     * @param pTypeProduit : TypeProduit :
     * Objet métier correspondant à l'Entity JPA TypeProduitJPA.
     * @param ctx : ConversionContext
     *
     * @return TypeProduitJPA : Entity JPA associé à l'objet métier.
     */
    private static TypeProduitJPA typeProduitMETIERToJPA(
            final TypeProduit pTypeProduit,
            final ConversionContext ctx) {

        /* ne fait rien et retourne null si pTypeProduit == null. */
        if (pTypeProduit == null) {
            return null;
        }

        /* ne fait rien et retourne null si
         * pTypeProduit.getTypeProduit() est blank (null ou espaces). */
        if (StringUtils.isBlank(pTypeProduit.getTypeProduit())) {
            return null;
        }

        /* retourne l'Entity JPA s'il était déjà dans le cache. */
        final TypeProduitJPA cached = ctx.get(pTypeProduit);
        if (cached != null) {
            return cached;
        }

        /* instancie un nouveau TypeProduitJPA. */
        final TypeProduitJPA typeProduitJPA = new TypeProduitJPA();

        /* met le nouveau TypeProduitJPA dans le cache. */
        ctx.put(pTypeProduit, typeProduitJPA);

        /* passe les paramètres scalaires au nouveau TypeProduitJPA. */
        typeProduitJPA.setIdTypeProduit(pTypeProduit.getIdTypeProduit());
        typeProduitJPA.setTypeProduit(pTypeProduit.getTypeProduit());

        /* CONVERTIT et ALIMENTE la liste ENFANT sousTypeProduits. */
        final List<? extends SousTypeProduitI> sousTypeProduits
            = pTypeProduit.getSousTypeProduits();

        /* alimente la liste sousTypeProduits du nouveau TypeProduitJPA
         * avec chaque sousTypeProduit converti.*/
        if (sousTypeProduits != null) {

            for (final SousTypeProduitI stp : sousTypeProduits) {

                if (stp == null) {
                    continue;
                }

                final SousTypeProduit otherMETIER = requireMetier(
                		stp
                		, SousTypeProduit.class
                		, "TypeProduit.sousTypeProduits -> élément");
                
                final SousTypeProduitI stpJPA
                	= sousTypeProduitMETIERToJPA(otherMETIER, ctx);

                if (stpJPA != null) {
                    // rattachement unitaire, idempotent, sans wipe
                    if (stpJPA.getTypeProduit() != typeProduitJPA) {
                    	stpJPA.setTypeProduit(typeProduitJPA);
                    }
                }
            }

        } else {

            /* LAZY-SAFE :
             * NE PAS "wiper" les collections JPA si la collection métier
             * est null / non chargée.
             * (conversion depuis enfant/leaf peut avoir déjà stabilisé le graphe).
             */
        	 /* ne pas faire : typeProduitJPA.setSousTypeProduits(null); */
        }

        return typeProduitJPA;
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme un objet métier 
     * <code style="font-weight:bold;">SousTypeProduit</code>
     * en Entity JPA 
     * <code style="font-weight:bold;">SousTypeProduitJPA</code>.</p>
     * <ul>
     * <li>ne fait rien et retourne null 
     * si pSousTypeProduit == null.</li>
     * <li>ne fait rien et retourne null si
     * pSousTypeProduit.getSousTypeProduit() est blank 
     * (null ou espaces).</li>
     * <li>retourne l'Entity JPA s'il était déjà dans le cache.</li>
     * <li>instancie un nouveau SousTypeProduitJPA.</li>
     * <li>met le nouveau SousTypeProduitJPA dans le cache.</li>
     * <li>passe les paramètres scalaires au nouveau SousTypeProduitJPA.</li>
     * <li>CONVERTIT le Parent TypeProduit (via la présente interface)</li>
     * <li>IMPORTANT : rattache le SousTypeProduitJPA 
     * au TypeProduitJPA UNE SEULE FOIS
     * via le setter canonique (idempotent) pour stabiliser le graphe.</li>
     * <li>CONVERTIT les Enfants Produit de la liste produits.</li>
     * <li>passe la liste des produits convertis au sousTypeProduitJPA
     * via le setter canonique de SousTypeProduitJPA.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
     * Diagramme de Classes du Produit qualifié par un SousTypeProduit qui est lui-même une déclinaison d'un TypeProduit</p>
     * <p>
     * <img src="../../../../../../../../../../../javadoc/images/persistence/metier/produittype/entities/entitiesJPA/diagramme_classes_produittype.jpg"
     * alt="diagramme de classes d'un Produit Typé" border="1" align="center" height= 200px />
     * </p>
     * </div>
     *
     * @param pSousTypeProduit : SousTypeProduit :
     * Objet métier correspondant à l'Entity JPA SousTypeProduitJPA.
     * @param ctx : ConversionContext
     *
     * @return SousTypeProduitJPA : Entity JPA associé à l'objet métier.
     */
    private static SousTypeProduitJPA sousTypeProduitMETIERToJPA(
            final SousTypeProduit pSousTypeProduit,
            final ConversionContext ctx) {

        /* ne fait rien et retourne null si pSousTypeProduit == null. */
        if (pSousTypeProduit == null) {
            return null;
        }

        /* ne fait rien et retourne null si
         * pSousTypeProduit.getSousTypeProduit() est blank 
         * (null ou espaces). */
        if (StringUtils.isBlank(
        		pSousTypeProduit.getSousTypeProduit())) {
            return null;
        }

        /* retourne l'Entity JPA s'il était déjà dans le cache. */
        final SousTypeProduitJPA cached = ctx.get(pSousTypeProduit);
        if (cached != null) {
            return cached;
        }

        /* instancie un nouveau SousTypeProduitJPA. */
        final SousTypeProduitJPA sousTypeProduitJPA 
        	= new SousTypeProduitJPA();

        /* met le nouveau SousTypeProduitJPA dans le cache. */
        ctx.put(pSousTypeProduit, sousTypeProduitJPA);

        /* passe les paramètres scalaires au nouveau SousTypeProduitJPA. */
        sousTypeProduitJPA.setIdSousTypeProduit(
        		pSousTypeProduit.getIdSousTypeProduit());
        
        /* IMPORTANT : nommer avant rattachement. */
        sousTypeProduitJPA.setSousTypeProduit(
        		pSousTypeProduit.getSousTypeProduit());

        /* CONVERSION du Parent TypeProduitJPA 
         * (via la présente interface)*** */
        final TypeProduitI typeProduitDansMETIER 
        	= pSousTypeProduit.getTypeProduit();
        
        final TypeProduit tpMETIER = requireMetier(
        		typeProduitDansMETIER
        		, TypeProduit.class
        		, "SousTypeProduit.typeProduit (parent)");

        /* IMPORTANT : conversion du parent
         * pour stabiliser le graphe et rattacher.*/
        final TypeProduitI tpJPA = typeProduitMETIERToJPA(tpMETIER, ctx);

        if (tpJPA != null) {
            if (sousTypeProduitJPA.getTypeProduit() != tpJPA) {
                sousTypeProduitJPA.setTypeProduit(tpJPA);
            }
        }

        /* IMPORTANT :
         * rattachement effectué ici via le setter canonique (idempotent),
         * afin de stabiliser le graphe. */

        // CONVERSION des Enfants Produits.************
        /* on stabilise le cache, MAIS on ne rattache PAS via
         * le Setter canonique sousTypeProduitJPA.setProduits(...)
         * car le rattachement est fait UNE SEULE FOIS côté enfant
         * dans produitJPA.setSousTypeProduit(...).*/
        final List<? extends ProduitI> produitsMETIER
            = pSousTypeProduit.getProduits();

        if (produitsMETIER != null) {

            for (final ProduitI produitMETIER : produitsMETIER) {

                if (produitMETIER == null) {
                    continue;
                }

                final Produit pMETIER = requireMetier(
                        produitMETIER
                        , Produit.class
                        , "SousTypeProduit.produits -> élément");

                /* Convertit et rattache via le setter canonique
                 * du ProduitJPA (produitJPA.setSousTypeProduit(stpJPA)) */
                produitMETIERToJPA(pMETIER, ctx);
            }
            
        } else {
        	
            /* LAZY-SAFE :
             * NE PAS "wiper" les collections JPA si la collection métier
             * est null / non chargée.
             * (conversion depuis enfant/leaf peut avoir 
             * déjà stabilisé le graphe).
             */
        	/* ne pas faire : sousTypeProduitJPA.setProduits(null); */
        }
        
        return sousTypeProduitJPA;
        
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme un objet métier 
     * <code style="font-weight:bold;">Produit</code>
     * en Entity JPA 
     * <code style="font-weight:bold;">ProduitJPA</code>.</p>
     * <ul>
     * <li>ne fait rien et retourne null si pProduit == null.</li>
     * <li>ne fait rien et retourne null si
     * pProduit.getProduit() est blank (null ou espaces).</li>
     * <li>retourne l'Entity JPA s'il était déjà dans le cache.</li>
     * <li>instancie un nouveau ProduitJPA.</li>
     * <li>met le nouveau ProduitJPA dans le cache.</li>
     * <li>passe les paramètres scalaires au nouveau ProduitJPA.</li>
     * <li>CONVERTIT le Parent SousTypeProduit 
     * (via la présente interface).</li>
     * <li>passe le parent SousTypeProduitJPA converti 
     * au ProduitJPA converti
     * via le setter canonique de ProduitJPA.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
     * Diagramme de Classes du Produit qualifié par un SousTypeProduit qui est lui-même une déclinaison d'un TypeProduit</p>
     * <p>
     * <img src="../../../../../../../../../../../javadoc/images/persistence/metier/produittype/entities/entitiesJPA/diagramme_classes_produittype.jpg"
     * alt="diagramme de classes d'un Produit Typé" border="1" align="center" height= 200px />
     * </p>
     * </div>
     *
     * @param pProduit : Produit :
     * Objet métier correspondant à l'Entity JPA ProduitJPA.
     * @param ctx : ConversionContext
     *
     * @return ProduitJPA : Entity JPA associé à l'objet métier.
     */
    private static ProduitJPA produitMETIERToJPA(
            final Produit pProduit,
            final ConversionContext ctx) {

        /* ne fait rien et retourne null si pProduit == null. */
        if (pProduit == null) {
            return null;
        }

        /* ne fait rien et retourne null si
         * pProduit.getProduit() est blank (null ou espaces). */
        if (StringUtils.isBlank(pProduit.getProduit())) {
            return null;
        }

        /* retourne l'Entity JPA s'il était déjà dans le cache. */
        final ProduitJPA cached = ctx.get(pProduit);
        if (cached != null) {
            return cached;
        }

        /* instancie un nouveau ProduitJPA. */
        final ProduitJPA produitJPA = new ProduitJPA();

        /* met le nouveau ProduitJPA dans le cache. */
        ctx.put(pProduit, produitJPA);

        /* passe les paramètres scalaires au nouveau ProduitJPA. */
        produitJPA.setIdProduit(pProduit.getIdProduit());
        produitJPA.setProduit(pProduit.getProduit());

        /* CONVERTIT le Parent SousTypeProduitJPA 
         * (via la présente interface). */
        final SousTypeProduit stpMETIER = requireMetier(
                pProduit.getSousTypeProduit()
                , SousTypeProduit.class
                , "Produit.sousTypeProduit (parent)");

        final SousTypeProduitI stpJpa
            = sousTypeProduitMETIERToJPA(stpMETIER, ctx);

        /* passe le parent SousTypeProduitJPA converti 
         * au ProduitJPA converti
         * via le setter canonique de ProduitJPA. */
        if (produitJPA.getSousTypeProduit() != stpJpa) {
            produitJPA.setSousTypeProduit(stpJpa);
        }

        return produitJPA;
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Définit une <span style="font-weight:bold;">
     * "égalité métier"</span> entre un objet métier 
     * <code style="font-weight:bold;">TypeProduit</code>
     * et une Entity JPA 
     * <code style="font-weight:bold;">TypeProduitJPA</code>.</p>
     * <p>l'égalité métier suppose de respecter au minimum
     * le contrat Java de equals(), à savoir que :
     * <ul>
     * <li>les typeProduit (String) sont les mêmes dans
     * l'objet métier et dans l'Entity JPA.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">pTypeProduit
     * et pTypeProduitJPA sont equalsMetier si : </p>
     * <ul>
     * <li style="font-weight:bold;">idTypeProduit == idTypeProduitJPA</li>
     * <li style="font-weight:bold;">typeProduit == typeProduitJPA (String)</li>
     * </ul>
     * </div>
     *
     * <div>
     * <ul>
     * <li>retourne true si les deux paramètres sont null.</li>
     * <li>retourne false si un seul des 2 paramètres est null.</li>
     * <li>retourne false si les idTypeProduit ne sont pas égaux.</li>
     * <li>retourne typeProduit.equals(typeProduitJPA)
     * les idTypeProduit étant identiques.</li>
     * </ul>
     * </div>
     *
     * @param pTypeProduit : TypeProduit
     * @param pTypeProduitJPA : TypeProduitJPA
     * @return boolean : true si equalsMetier
     */
    public static boolean equalsMetier(
            final TypeProduitI pTypeProduit,
            final TypeProduitJPA pTypeProduitJPA) {

        /* retourne true si les deux paramètres sont null.*/
        /* retourne false si un seul des 2 paramètres est null. */
        if (pTypeProduit == null) {
            return pTypeProduitJPA == null;
        }

        if (pTypeProduitJPA == null) {
            return false;
        }

        /* idTypeProduit. */
        /* retourne false si les idTypeProduit ne sont pas égaux. */
        if (!Objects.equals(pTypeProduit.getIdTypeProduit(),
                pTypeProduitJPA.getIdTypeProduit())) {
            return false;
        }

        /* typeProduit. */
        /* retourne typeProduit.equals(typeProduitJPA).*/
        return Strings.CI.equals(
                pTypeProduit.getTypeProduit(),
                pTypeProduitJPA.getTypeProduit());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Définit une <span style="font-weight:bold;">
     * "égalité métier"</span> entre un objet métier 
     * <code style="font-weight:bold;">SousTypeProduit</code>
     * et une Entity JPA 
     * <code style="font-weight:bold;">SousTypeProduitJPA</code>.</p>
     * <p>l'égalité métier suppose de respecter au minimum
     * le contrat Java de equals(), à savoir que :
     * <ul>
     * <li>les typeProduit du SousTypeProduit
     * doivent être "equalsMetier" dans l'objet métier
     * et dans l'Entity JPA.</li>
     * <li>les sousTypeProduit (String) sont les mêmes dans
     * l'objet métier et dans l'Entity JPA.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">pSousTypeProduit.equalsMetier(
     * pSousTypeProduitJPA) si :</p>
     * <ul>
     * <li style="font-weight:bold;">
     * idSousTypeProduit == idSousTypeProduitJPA</li>
     * <li style="font-weight:bold;">
     * typeProduit.equalsMetier(typeProduitJPA)</li>
     * <li style="font-weight:bold;">
     * soustypeProduitString.equals(soustypeProduitStringJPA)</li>
     * </ul>
     * </div>
     *
     * <div>
     * <ul>
     * <li>retourne true si les deux paramètres sont null.</li>
     * <li>retourne false si un seul des 2 paramètres est null.</li>
     * <li>retourne false si les idSousTypeProduit
     * ne sont pas égaux.</li>
     * <li>retourne false si les deux TypeProduit
     * ne sont pas equalsMetier.</li>
     * <li>retourne la comparaison entre les String
     * soustypeProduitString et soustypeProduitStringJPA
     * (les idSousTypeProduit et TypeProduit étant equals par ailleurs).</li>
     * </ul>
     * </div>
     *
     * @param pSousTypeProduit : SousTypeProduit
     * @param pSousTypeProduitJPA : SousTypeProduitJPA
     *
     * @return boolean : true si equalsMetier.
     */
    public static boolean equalsMetier(
            final SousTypeProduitI pSousTypeProduit,
            final SousTypeProduitJPA pSousTypeProduitJPA) {

        /* retourne true si les deux paramètres sont null.*/
        /* retourne false si un seul des 2 paramètres est null.*/
        if (pSousTypeProduit == null) {
            return pSousTypeProduitJPA == null;
        }
        if (pSousTypeProduitJPA == null) {
            return false;
        }

        /* idSousTypeProduit. */
        /* retourne false si les idSousTypeProduit ne sont pas égaux. */
        if (!Objects.equals(
                pSousTypeProduit.getIdSousTypeProduit(),
                pSousTypeProduitJPA.getIdSousTypeProduit())) {
            return false;
        }

        /* TypeProduit. */
        /* retourne false si les deux TypeProduit
         * ne sont pas equalsMetier. */
        final TypeProduitI typeProduit = pSousTypeProduit.getTypeProduit();
        final TypeProduitJPA typeProduitJPA
        = (pSousTypeProduitJPA.getTypeProduit()
                instanceof TypeProduitJPA tp) ? tp : null;

        if (!equalsMetier(typeProduit, typeProduitJPA)) {
            return false;
        }

        /* sousTypeProduit. */
        /* retourne la comparaison entre les String
         * soustypeProduitString et soustypeProduitStringJPA. */
        return Strings.CI.equals(
                pSousTypeProduit.getSousTypeProduit(),
                pSousTypeProduitJPA.getSousTypeProduit());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Définit une <span style="font-weight:bold;">
     * "égalité métier"</span>
     * entre un objet métier 
     * <code style="font-weight:bold;">Produit</code>
     * et une Entity JPA 
     * <code style="font-weight:bold;">ProduitJPA</code>.</p>
     * <p>l'égalité métier suppose de respecter au minimum
     * le contrat Java de equals(), à savoir que :
     * <ul>
     * <li>les SousTypeProduit du TypeProduit
     * doivent être "equalsMetier" dans l'objet métier
     * et dans l'Entity JPA.</li>
     * <li>les Produit (String) sont les mêmes dans
     * l'objet métier et dans l'Entity JPA.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">
     * pProduit.equalsMetier(pProduitJPA) si :</p>
     * <ul>
     * <li style="font-weight:bold;">
     * idProduit == idProduitJPA</li>
     * <li style="font-weight:bold;">
     * sousTypeProduit.equalsMetier(sousTypeProduitJPA)</li>
     * <li style="font-weight:bold;">
     * produitString.equals(produitStringJPA)</li>
     * </ul>
     * </div>
     *
     * <div>
     * <ul>
     * <li>retourne true si les deux paramètres sont null.</li>
     * <li>retourne false si un seul des 2 paramètres est null.</li>
     * <li>retourne false si les idProduit ne sont pas égaux.</li>
     * <li>retourne false si les deux SousTypeProduit
     * ne sont pas equalsMetier.</li>
     * <li>retourne la comparaison entre les String
     * produitString et produitStringJPA
     * (les idProduit et SousTypeProduit étant equals par ailleurs).</li>
     * </ul>
     * </div>
     *
     * @param pProduit : ProduitI
     * @param pProduitJPA : ProduitJPA
     * @return true si equalsMetier
     */
    public static boolean equalsMetier(
            final ProduitI pProduit,
            final ProduitJPA pProduitJPA) {

        /* retourne true si les deux paramètres sont null.*/
        /* retourne false si un seul des 2 paramètres est null.*/
        if (pProduit == null) {
            return pProduitJPA == null;
        }
        if (pProduitJPA == null) {
            return false;
        }

        /* idProduit. */
        /* retourne false si les idProduit ne sont pas égaux. */
        if (!Objects.equals(
                pProduit.getIdProduit(),
                pProduitJPA.getIdProduit())) {
            return false;
        }

        /* SousTypeProduit. */
        /* retourne false si les deux SousTypeProduit
         * ne sont pas equalsMetier. */
        final SousTypeProduitI stp = pProduit.getSousTypeProduit();
        final SousTypeProduitJPA stpJPA
        = (pProduitJPA.getSousTypeProduit()
                instanceof SousTypeProduitJPA stp2) ? stp2 : null;

        if (!equalsMetier(stp, stpJPA)) {
            return false;
        }

        /* produit (String). */
        /* retourne la comparaison entre les String produitString. */
        return Strings.CI.equals(
                pProduit.getProduit(),
                pProduitJPA.getProduit());
    }

    
    
    /**
	 *
	 * @param <T> : T
	 * @param o : Object
	 * @param expected : Class<T>
	 * @param contexte String
	 * @return T
	 */
	private static <T> T requireMetier(
	        final Object o,
	        final Class<T> expected,
	        final String contexte) {
	
	    if (o == null) {
	        throw new IllegalStateException(contexte + " : objet null");
	    }
	
	    if (!expected.isInstance(o)) {
	        throw new IllegalStateException(
	            IMPLEMENTATION_NON_METIER + "attendue = " 
	            + expected.getSimpleName()
	            + " / trouvée=" + o.getClass().getName()
	            + " / contexte=" + contexte);
	    }
	
	    return expected.cast(o);
	}



	/**
     * <div>
     * <p style="font-weight:bold;">
     * retourne une String formatée pour l'affichage
     * d'un TypeProduit</p>
     * <ul>
     * <li>retourne null si pTypeProduit == null.</li>
     * <li>affiche le TypeProduit</li>
     * <li>affiche la liste des SousTypeProduit contenus
     * dans le TypeProduit</li>
     * <li>affiche pour chaque SousTypeProduit la liste des Produit qu'il contient.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p style="text-decoration:underline;">Exemple d'affichage : </p>
     * <pre>******* TypeProduit : vêtement *******
     * [idTypeProduit : 1 - typeProduit : vêtement  ]
     *
     * ******* sousTypeProduits du TypeProduit : vêtement
     * [idSousTypeProduit : 1 - sousTypeProduit : vêtement pour homme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduit : vêtement pour homme
     * [idProduit dans produits du SousTypeProduit : 1 - produit dans produits du SousTypeProduit : chemise à manches longues pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 2 - produit dans produits du SousTypeProduit : chemise à manches courtes pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 3 - produit dans produits du SousTypeProduit : sweatshirt pour homme                    - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 4 - produit dans produits du SousTypeProduit : teeshirt pour homme                      - sousTypeProduit dans le produit : vêtement pour homme ]
     *
     * [idSousTypeProduit : 2 - sousTypeProduit : vêtement pour femme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduit : vêtement pour femme
     * null
     *
     * [idSousTypeProduit : 3 - sousTypeProduit : vêtement pour enfant - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduit : vêtement pour enfant
     * null
     * </pre>
     * </div>
     *
     * @param pTypeProduit : TypeProduitI : Objet métier à afficher
     * @return String
     */
    public static String afficherTypeProduitFormate(
            final TypeProduitI pTypeProduit) {

        /* retourne null si pTypeProduit == null. */
        if (pTypeProduit == null) {
            return null;
        }

        final Long idTypeProduit = pTypeProduit.getIdTypeProduit();
        final String typeProduitString 
        	= pTypeProduit.getTypeProduit();
        
        final List<? extends SousTypeProduitI> sousTypeProduits 
        	= pTypeProduit.getSousTypeProduits();

        final StringBuilder stb = new StringBuilder();

        stb.append("******* TypeProduit : ");
        stb.append(typeProduitString);
        stb.append(" *******");
        stb.append(SAUT_DE_LIGNE);

        final String pres1 
        	= String.format("[idTypeProduit : "
        			+ FORMAT_ID
        			+ TIRET_ESPACE
        			+ "typeProduit"
        			+ DEUX_POINTS_ESPACE
        			+ FORMAT_TP
        			+ CROCHET_FERMANT
                , idTypeProduit
                , typeProduitString);
        
        stb.append(pres1);
        stb.append(SAUT_DE_LIGNE);
        stb.append(SAUT_DE_LIGNE);

        stb.append("******* sousTypeProduits du TypeProduit : ");
        stb.append(typeProduitString);
        stb.append(SAUT_DE_LIGNE);

        if (sousTypeProduits == null) {
            stb.append(NULL);
        } else {
        	
            for (final SousTypeProduitI 
            		sousTypeProduit : sousTypeProduits) {
            	
                final Long idSousTypeProduit 
                	= sousTypeProduit.getIdSousTypeProduit();
                final String sousTypeProduitString 
                	= sousTypeProduit.getSousTypeProduit();
                
                /* Cast par pattern matching (canonique + sans cast risqué)*/
                final TypeProduitI tpI = sousTypeProduit.getTypeProduit();
                final TypeProduit typeProduitduSousTypeProduit =
                        (tpI instanceof TypeProduit other) ? other : null;

                final List<? extends ProduitI> produitsDansSousProduit 
                	= sousTypeProduit.getProduits();

                Long idTypeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;

                if (typeProduitduSousTypeProduit != null) {
                    idTypeProduitduSousTypeProduit 
                    	= typeProduitduSousTypeProduit.getIdTypeProduit();
                    typeProduitduSousTypeProduitString 
                    	= typeProduitduSousTypeProduit.getTypeProduit();
                }

                final String pres2 = String.format(
                        "[idSousTypeProduit : "
                        + FORMAT_ID
                        + TIRET_ESPACE
                        + "sousTypeProduit : "
                        + FORMAT_STP
                        + TIRET_ESPACE
                        + "[idTypeProduit du TypeProduit dans le SousTypeProduit : "
                        + FORMAT_ID
                        + TIRET_ESPACE
                        + "typeProduitString du TypeProduit dans le SousTypeProduit : "
                        + FORMAT_TP
                        + "]]"
                        , idSousTypeProduit
                        , sousTypeProduitString
                        , idTypeProduitduSousTypeProduit
                        , typeProduitduSousTypeProduitString);

                stb.append(pres2);
                stb.append(SAUT_DE_LIGNE);

                stb.append("***** liste des produits dans le sousProduit : ");
                stb.append(sousTypeProduitString);
                stb.append(SAUT_DE_LIGNE);
                if (produitsDansSousProduit == null) {
                    stb.append(NULL);
                    stb.append(SAUT_DE_LIGNE);
                } else {
                    for (final ProduitI produit : produitsDansSousProduit) {
                    	
                        final Long idProduit = produit.getIdProduit();
                        final String produitString 
                        	= produit.getProduit();
                                              
                        /*Cast par pattern matching 
                         * (canonique + sans cast risqué)*/
                        final SousTypeProduitI stpI 
                        	= produit.getSousTypeProduit();
                        final SousTypeProduit sousTypeProduitMETIERProduit =
                                (stpI instanceof SousTypeProduit other) 
                                ? other : null;

                        String sousTypeProduitMETIERProduitString = null;

                        if (sousTypeProduitMETIERProduit != null) {
                            sousTypeProduitMETIERProduitString
                                = sousTypeProduitMETIERProduit.getSousTypeProduit();
                        }

                        stb.append('\t');
                        
                        final String presProduit 
                        = String.format(
                                "[idProduit dans produits du SousTypeProduit : "
                                + FORMAT_ID
                                + TIRET_ESPACE
                                + "produit dans produits du SousTypeProduit : "
                                + FORMAT_P
                                + TIRET_ESPACE
                                + "sousTypeProduit dans le produit : "
                                + FORMAT_STP
                                + CROCHET_FERMANT
                                , idProduit
                                , produitString
                                , sousTypeProduitMETIERProduitString);

                        stb.append(presProduit);
                        stb.append(SAUT_DE_LIGNE);
                    }
                }
                stb.append(SAUT_DE_LIGNE);
            }
        }
        
        return stb.toString();
        
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * retourne une String formatée pour l'affichage
     * d'un TypeProduitJPA</p>
     * <ul>
     * <li>retourne null si pTypeProduitJPA == null.</li>
     * <li>affiche le TypeProduitJPA</li>
     * <li>affiche la liste des SousTypeProduitJPA contenus
     * dans le TypeProduitJPA</li>
     * <li>affiche pour chaque SousTypeProduitJPA 
     * la liste des ProduitJPA qu'il contient.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p style="text-decoration:underline;">Exemple d'affichage : </p>
     * <pre>******* TypeProduitJPA : vêtement *******
     * [idTypeProduit : 1 - typeProduit : vêtement  ]
     *
     * ******* sousTypeProduitsJPA du TypeProduitJPA : vêtement
     * [idSousTypeProduit : 1 - sousTypeProduit : vêtement pour homme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduitJPA : vêtement pour homme
     * [idProduit dans produits du SousTypeProduit : 1 - produit dans produits du SousTypeProduit : chemise à manches longues pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 2 - produit dans produits du SousTypeProduit : chemise à manches courtes pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 3 - produit dans produits du SousTypeProduit : sweatshirt pour homme                    - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 4 - produit dans produits du SousTypeProduit : teeshirt pour homme                      - sousTypeProduit dans le produit : vêtement pour homme ]
     *
     * [idSousTypeProduit : 2 - sousTypeProduit : vêtement pour femme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduitJPA : vêtement pour femme
     * null
     *
     * [idSousTypeProduit : 3 - sousTypeProduit : vêtement pour enfant - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduitJPA : vêtement pour enfant
     * null
     * </pre>
     * </div>
     *
     * @param pTypeProduitJPA : TypeProduitJPA : Entity JPA à afficher
     * @return String
     */
    public static String afficherTypeProduitFormate(
            final TypeProduitJPA pTypeProduitJPA) {

        /* retourne null si pTypeProduitJPA == null. */
        if (pTypeProduitJPA == null) {
            return null;
        }

        final Long idTypeProduitJPA = pTypeProduitJPA.getIdTypeProduit();
        final String typeProduitStringJPA 
        	= pTypeProduitJPA.getTypeProduit();
        final List<? extends SousTypeProduitI> sousTypeProduitsJPA 
        	= pTypeProduitJPA.getSousTypeProduits();

        final StringBuilder stb = new StringBuilder();

        stb.append("******* TypeProduitJPA : ");
        stb.append(typeProduitStringJPA);
        stb.append(" *******");
        stb.append(SAUT_DE_LIGNE);

        final String pres1 = String.format(
                "[idTypeProduit : "
                + FORMAT_ID
                + TIRET_ESPACE
                + "typeProduit : "
                + FORMAT_TP
                + CROCHET_FERMANT
                , idTypeProduitJPA
                , typeProduitStringJPA);
        
        stb.append(pres1);
        stb.append(SAUT_DE_LIGNE);
        stb.append(SAUT_DE_LIGNE);

        stb.append("******* sousTypeProduitsJPA du TypeProduitJPA : ");
        stb.append(typeProduitStringJPA);
        stb.append(SAUT_DE_LIGNE);

        if (sousTypeProduitsJPA == null) {
            stb.append(NULL);
        } else {
        	
            for (final SousTypeProduitI 
            		sousTypeProduitJPA : sousTypeProduitsJPA) {
            	
                final Long idSousTypeProduitJPA 
                	= sousTypeProduitJPA.getIdSousTypeProduit();
                final String sousTypeProduitJPAString 
                	= sousTypeProduitJPA.getSousTypeProduit();
                
                /* Cast par pattern matching (canonique + sans cast risqué) */
                final TypeProduitI tpSTPi = sousTypeProduitJPA.getTypeProduit();
                final TypeProduitJPA typeProduitduSousTypeProduit =
                        (tpSTPi instanceof TypeProduitJPA other) ? other : null;

                final List<? extends ProduitI> produitsDansSousProduit
                    = sousTypeProduitJPA.getProduits();

                Long idTypeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;

                if (typeProduitduSousTypeProduit != null) {
                    idTypeProduitduSousTypeProduit
                        = typeProduitduSousTypeProduit.getIdTypeProduit();
                    typeProduitduSousTypeProduitString
                        = typeProduitduSousTypeProduit.getTypeProduit();
                }

                final String pres2 
                 = String.format("[idSousTypeProduit : "
                 		+ FORMAT_ID
                 		+ TIRET_ESPACE
                 		+ "sousTypeProduit : "
                 		+ FORMAT_STP
                 		+ TIRET_ESPACE
                 		+ "[idTypeProduit du TypeProduit dans le SousTypeProduit : "
                 		+ FORMAT_ID
                 		+ TIRET_ESPACE
                 		+ "typeProduitString du TypeProduit dans le SousTypeProduit : "
                 		+ FORMAT_TP
                 		+ "]]"
                        , idSousTypeProduitJPA
                        , sousTypeProduitJPAString
                        , idTypeProduitduSousTypeProduit
                        , typeProduitduSousTypeProduitString);

                stb.append(pres2);
                stb.append(SAUT_DE_LIGNE);

                stb.append("***** liste des produits dans le sousProduitJPA : ");
                stb.append(sousTypeProduitJPAString);
                stb.append(SAUT_DE_LIGNE);
                if (produitsDansSousProduit == null) {
                    stb.append(NULL);
                    stb.append(SAUT_DE_LIGNE);
                } else {
                	
                    for (final ProduitI produitJPA : produitsDansSousProduit) {
                    	
                        final Long idProduitJPA = produitJPA.getIdProduit();
                        final String produitJPAString = produitJPA.getProduit();
                        final SousTypeProduitI sousTypeProduitJPAProduit 
                        	= produitJPA.getSousTypeProduit();

                        String sousTypeProduitJPAProduitString = null;
                        
                        if (sousTypeProduitJPAProduit != null) {
                            sousTypeProduitJPAProduitString 
                            	= sousTypeProduitJPAProduit.getSousTypeProduit();
                        }

                        stb.append('\t');
                        
                        final String presProduit 
                        = String.format("[idProduit dans produits du SousTypeProduit : "
                        		+ FORMAT_ID
                        		+ TIRET_ESPACE
                        		+ "produit dans produits du SousTypeProduit : "
                        		+ FORMAT_P
                        		+ TIRET_ESPACE
                        		+ "sousTypeProduit dans le produit : "
                        		+ FORMAT_STP
                        		+ CROCHET_FERMANT
                                , idProduitJPA
                                , produitJPAString
                                , sousTypeProduitJPAProduitString);

                        stb.append(presProduit);
                        stb.append(SAUT_DE_LIGNE);
                    }
                }
                stb.append(SAUT_DE_LIGNE);
            }
        }
        
        return stb.toString();
        
    }

    
     
    /**
     * <div>
     * <p style="font-weight:bold;">
     * fournit une String pour l'affichage à la console
     * d'une List&lt;ProduitJPA&gt;</p>
     * </div>
     *
     * @param pList : List&lt;ProduitJPA&gt;
     *
     * @return String
     */
    private String afficherProduitsJPA(final List<ProduitJPA> pList) {

        if (pList == null) {
            return null;
        }

        final StringBuilder stb = new StringBuilder();

        for (final ProduitJPA produit : pList) {

            if (produit != null) {

                /* idProduit. */
                final Long idProduit = produit.getIdProduit();

                /* produit. */
                final String produitString = produit.getProduit();

                /* SOUSTYPEPRODUIT.*/
                final SousTypeProduitJPA sousTypeProduit
                    = (SousTypeProduitJPA) produit.getSousTypeProduit();

                /* sousTypeProduit. */
                Long idSousProduit = null;
                String sousTypeProduitString = null;
                TypeProduitJPA typeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;

                if (sousTypeProduit != null) {
                    idSousProduit
                        = sousTypeProduit.getIdSousTypeProduit();
                    sousTypeProduitString
                        = sousTypeProduit.getSousTypeProduit();
                    typeProduitduSousTypeProduit
                        = (TypeProduitJPA) sousTypeProduit.getTypeProduit();
                    if (typeProduitduSousTypeProduit != null) {
                        typeProduitduSousTypeProduitString
                            = typeProduitduSousTypeProduit.getTypeProduit();
                    }
                }

                /* TYPEPRODUIT*/
                final TypeProduitI typeProduit = produit.getTypeProduit();

                Long idTypeProduit = null;
                String typeProduitString = null;

                if (typeProduit != null) {
                    idTypeProduit = typeProduit.getIdTypeProduit();
                    typeProduitString = typeProduit.getTypeProduit();
                }

                final String presentation
                    = String.format(
                            "idProduit : "
                            + FORMAT_IDPRODUIT
                            + " - produit : "
                            + "%-40s"
                            + " - [idSousProduit : "
                            + FORMAT_IDSTP
                            + SOUS_TYPE_PRODUIT
                            + FORMAT_STP
                            + " - typeProduit du sousTypeProduit : "
                            + "%-12s"
                            + "] - [idTypeProduit du Produit : "
                            + FORMAT_IDTP
                            + " - typeProduit du Produit : "
                            + "%-12s]"
                            , idProduit
                            , produitString
                            , idSousProduit
                            , sousTypeProduitString
                            , typeProduitduSousTypeProduitString
                            , idTypeProduit
                            , typeProduitString);

                stb.append(presentation);
                stb.append(SAUT_DE_LIGNE);

            }

        }

        return stb.toString();

    }

    
      
    /**
     * <div>
     * <p style="font-weight:bold;">
     * fournit une String pour l'affichage à la console
     * d'une List&lt;Produit&gt;</p>
     * </div>
     *
     * @param pList : List&lt;Produit&gt;
     *
     * @return String
     */
    private String afficherProduits(final List<Produit> pList) {

        if (pList == null) {
            return null;
        }

        final StringBuilder stb = new StringBuilder();

        for (final Produit produit : pList) {

            if (produit != null) {

                /* idProduit. */
                final Long idProduit = produit.getIdProduit();

                /* produit. */
                final String produitString = produit.getProduit();

                /* SOUSTYPEPRODUIT.*/
                final SousTypeProduitJPA sousTypeProduit
                    = (SousTypeProduitJPA) produit.getSousTypeProduit();

                /* sousTypeProduit. */
                Long idSousProduit = null;
                String sousTypeProduitString = null;
                TypeProduitJPA typeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;

                if (sousTypeProduit != null) {
                    idSousProduit
                        = sousTypeProduit.getIdSousTypeProduit();
                    sousTypeProduitString
                        = sousTypeProduit.getSousTypeProduit();
                    typeProduitduSousTypeProduit
                        = (TypeProduitJPA) sousTypeProduit.getTypeProduit();
                    if (typeProduitduSousTypeProduit != null) {
                        typeProduitduSousTypeProduitString
                            = typeProduitduSousTypeProduit.getTypeProduit();
                    }
                }

                /* TYPEPRODUIT*/
                final TypeProduitI typeProduit = produit.getTypeProduit();

                Long idTypeProduit = null;
                String typeProduitString = null;

                if (typeProduit != null) {
                    idTypeProduit = typeProduit.getIdTypeProduit();
                    typeProduitString = typeProduit.getTypeProduit();
                }

                final String presentation
                    = String.format(
                            "idProduit : "
                            + FORMAT_IDPRODUIT
                            + " - produit : "
                            + "%-40s"
                            + " - [idSousProduit : "
                            + FORMAT_IDSTP
                            + SOUS_TYPE_PRODUIT
                            + FORMAT_STP
                            + " - typeProduit du sousTypeProduit : "
                            + "%-12s"
                            + "] - [idTypeProduit du Produit : "
                            + FORMAT_IDTP
                            + " - typeProduit du Produit : "
                            + "%-12s]"
                            , idProduit
                            , produitString
                            , idSousProduit
                            , sousTypeProduitString
                            , typeProduitduSousTypeProduitString
                            , idTypeProduit
                            , typeProduitString);

                stb.append(presentation);
                stb.append(SAUT_DE_LIGNE);

            }

        }

        return stb.toString();

    }

    
    

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * fournit une String formatée pour l'affichage à la console
     * d'une List&lt;SousTypeProduitJPA&gt;</p>
     * </div>
     *
     * @param pList : List&lt;SousTypeProduitJPA&gt;
     *
     * @return String
     */
    private String afficherSousTypeProduitsJPA(
    		final List<? extends SousTypeProduitI> pList) {

        if (pList == null) {
            return null;
        }

        final StringBuilder stb = new StringBuilder();

        for (final SousTypeProduitI sousTypeProduit : pList) {

            final Long idSousTypeProduit
                = sousTypeProduit.getIdSousTypeProduit();
            final String sousTypeProduitString
                = sousTypeProduit.getSousTypeProduit();
            final String typeProduit
                = sousTypeProduit.getTypeProduit().getTypeProduit();

            final String presentation
                = String.format(
                        "idSousTypeProduit : "
                        + "%-1s"
                        + SOUS_TYPE_PRODUIT
                        + "%-30s"
                        + " - typeProduit : "
                        + FORMAT_STP
                        , idSousTypeProduit
                        , sousTypeProduitString
                        , typeProduit);

            stb.append(presentation);
            stb.append(SAUT_DE_LIGNE);
        }

        return stb.toString();

    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * fournit une String formatée pour l'affichage à la console
     * d'une List&lt;SousTypeProduit&gt;</p>
     * </div>
     *
     * @param pList : List&lt;SousTypeProduit&gt;
     *
     * @return String
     */
    private String afficherSousTypeProduits(
    		final List<SousTypeProduit> pList) {

        if (pList == null) {
            return null;
        }

        final StringBuilder stb = new StringBuilder();

        for (final SousTypeProduit sousTypeProduit : pList) {

            final Long idSousTypeProduit
                = sousTypeProduit.getIdSousTypeProduit();
            final String sousTypeProduitString
                = sousTypeProduit.getSousTypeProduit();
            final String typeProduit
                = sousTypeProduit.getTypeProduit().getTypeProduit();

            final String presentation
                = String.format(
                        "idSousTypeProduit : "
                        + "%-1s"
                        + SOUS_TYPE_PRODUIT
                        + "%-30s"
                        + " - typeProduit : "
                        + "%-20s"
                        , idSousTypeProduit
                        , sousTypeProduitString
                        , typeProduit);

            stb.append(presentation);
            stb.append(SAUT_DE_LIGNE);
        }

        return stb.toString();

    }
    
}
