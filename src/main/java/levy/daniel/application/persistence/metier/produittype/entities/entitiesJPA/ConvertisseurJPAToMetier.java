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
 * <p style="font-weight:bold;">CLASSE ConvertisseurJPAToMetier.java :</p>
 *
 * <p>
 * Cette classe final est une classe UTILITAIRE chargée de
 * <span style="font-weight:bold;">convertir des Entity JPA</span>
 * en <span style="font-weight:bold;">objets métier</span>
 * </p>
 *
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemple d'utilisation : </p>
 * <ul>
 * <li><code>// retourne un TypeProduit après
 * conversion d'un TypeProduitJPA</code>.</li>
 * <li><code>final TypeProduit typeProduitPeche
 * = ConvertisseurJPAToMetier.typeProduitJPAToMetier(
 * new TypeProduitJPA("Pêche"));</code></li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 30 déc. 2025
 */
public final class ConvertisseurJPAToMetier {

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
     * "Implémentation non-JPA "
     */
    public static final String IMPLEMENTATION_NON_JPA
        = "Implémentation non-JPA ";

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
            .getLogger(ConvertisseurJPAToMetier.class);

    // *************************METHODES**********************************/

    /**
    * <div>
    * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
    * <p>Constructeur private pour bloquer l'instanciation de la classe.</p>
    * </div>
    */
    private ConvertisseurJPAToMetier() {
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
         * {@code ConvertisseurJPAToMetier} {@link SHARED_CACHE}</li>
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
             * de la classe englobante ConvertisseurJPAToMetier : 
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
         * {@code ConvertisseurJPAToMetier} {@link SHARED_CACHE}.</li>
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
     * transforme une Entity JPA <code style="font-weight:bold;">
     * TypeProduitJPA</code>
     * en objet métier <code style="font-weight:bold;">
     * TypeProduit</code>.</p>
     * </div>
     *
     * @param pTypeProduitJPA : TypeProduitJPA :
     * Entity JPA correspondant à l'objet métier TypeProduit.
     * @return TypeProduit : objet métier associé à l'Entity JPA.
     */
    public static TypeProduit typeProduitJPAToMetier(
            final TypeProduitJPA pTypeProduitJPA) {
        return typeProduitJPAToMetier(
        		pTypeProduitJPA, new ConversionContext());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme une Entity JPA
     * <code style="font-weight:bold;">SousTypeProduitJPA</code>
     * en objet métier
     * <code style="font-weight:bold;">SousTypeProduit</code></p>
     * </div>
     *
     * @param pSousTypeProduitJPA : SousTypeProduitJPA :
     * Entity JPA correspondant à l'objet métier SousTypeProduit.
     * @return SousTypeProduit : objet métier associé à l'Entity JPA.
     */
    public static SousTypeProduit sousTypeProduitJPAToMetier(
            final SousTypeProduitJPA pSousTypeProduitJPA) {
        return sousTypeProduitJPAToMetier(
        		pSousTypeProduitJPA, new ConversionContext());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme une Entity JPA
     * <code style="font-weight:bold;">ProduitJPA</code>
     * en objet métier
     * <code style="font-weight:bold;">Produit</code></p>
     * </div>
     *
     * @param pProduitJPA :
     * Entity JPA correspondant à l'objet métier Produit.
     * @return Produit : 
     * objet métier associé à l'Entity JPA.
     */
    public static Produit produitJPAToMetier(
            final ProduitJPA pProduitJPA) {
        return produitJPAToMetier(pProduitJPA, new ConversionContext());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme une Entity JPA <code style="font-weight:bold;">
     * TypeProduitJPA</code>
     * en objet métier <code style="font-weight:bold;">
     * TypeProduit</code>.</p>
     * <ul>
     * <li>ne fait rien et retourne null si pTypeProduitJPA == null.</li>
     * <li>ne fait rien et retourne null si
     * pTypeProduitJPA.getTypeProduit() est blank (null ou espaces).</li>
     * <li>retourne l'objet métier s'il était déjà dans le cache.</li>
     * <li>instancie un nouveau TypeProduit.</li>
     * <li>met le nouveau TypeProduit dans le cache.</li>
     * <li>passe les paramètres scalaires au nouveau TypeProduit.</li>
     * <li>CONVERTIT et ALIMENTE la liste ENFANT sousTypeProduits</li>
     * <li>passe la nouvelle liste des sousTypeProduits convertis au
     * nouveau TypeProduit.</li>
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
     * @param pTypeProduitJPA : TypeProduitJPA :
     * Entity JPA correspondant à l'objet métier TypeProduit.
     * @param ctx : ConversionContext
     *
     * @return TypeProduit : objet métier associé à l'Entity JPA.
     */
    private static TypeProduit typeProduitJPAToMetier(
            final TypeProduitJPA pTypeProduitJPA,
            final ConversionContext ctx) {

        /* ne fait rien et retourne null si pTypeProduitJPA == null. */
        if (pTypeProduitJPA == null) {
            return null;
        }

        /* ne fait rien et retourne null si
         * pTypeProduitJPA.getTypeProduit() est blank (null ou espaces). */
        if (StringUtils.isBlank(pTypeProduitJPA.getTypeProduit())) {
            return null;
        }

        /* retourne l'objet métier s'il était déjà dans le cache. */
        final TypeProduit cached = ctx.get(pTypeProduitJPA);
        if (cached != null) {
            return cached;
        }

        /* instancie un nouveau TypeProduit. */
        final TypeProduit typeProduit = new TypeProduit();

        /* met le nouveau TypeProduit dans le cache. */
        ctx.put(pTypeProduitJPA, typeProduit);

        /* passe les paramètres scalaires au nouveau TypeProduit. */
        typeProduit.setIdTypeProduit(pTypeProduitJPA.getIdTypeProduit());
        typeProduit.setTypeProduit(pTypeProduitJPA.getTypeProduit());

        /* CONVERTIT et ALIMENTE la liste ENFANT sousTypeProduits. */
        final List<? extends SousTypeProduitI> sousTypeProduitsJPA
            = pTypeProduitJPA.getSousTypeProduits();

        /* alimente la liste sousTypeProduits du nouveau TypeProduit
         * avec chaque sousTypeProduit converti.*/
        if (sousTypeProduitsJPA != null) {

            for (final SousTypeProduitI stpJPA : sousTypeProduitsJPA) {

                if (stpJPA == null) {
                    continue;
                }

                final SousTypeProduitJPA otherJPA = requireJPA(
                        stpJPA
                        , SousTypeProduitJPA.class
                        , "TypeProduitJPA.sousTypeProduits -> élément");

                final SousTypeProduitI stpMetier
                    = sousTypeProduitJPAToMetier(otherJPA, ctx);

                if (stpMetier != null) {
                    // rattachement unitaire, idempotent, sans wipe
                    if (stpMetier.getTypeProduit() != typeProduit) {
                        stpMetier.setTypeProduit(typeProduit);
                    }
                }
            }

        } else {

            /* LAZY-SAFE :
             * NE PAS "wiper" les collections métier si la collection JPA
             * est null / non chargée.
             * (conversion depuis enfant/leaf peut avoir 
             * déjà stabilisé le graphe).
             */
            /* ne pas faire : typeProduit.setSousTypeProduits(null); */
        }

        return typeProduit;

    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme une Entity JPA
     * <code style="font-weight:bold;">SousTypeProduitJPA</code>
     * en objet métier
     * <code style="font-weight:bold;">SousTypeProduit</code></p>
     * <ul>
     * <li>ne fait rien et retourne null 
     * si pSousTypeProduitJPA == null.</li>
     * <li>ne fait rien et retourne null si
     * pSousTypeProduitJPA.getSousTypeProduit() est blank 
     * (null ou espaces).</li>
     * <li>retourne l'objet métier s'il était déjà dans le cache.</li>
     * <li>instancie un nouveau SousTypeProduit.</li>
     * <li>met le nouveau SousTypeProduit dans le cache.</li>
     * <li>passe les paramètres scalaires au nouveau SousTypeProduit.</li>
     * <li>CONVERTIT le Parent TypeProduit (via la présente interface)</li>
     * <li>IMPORTANT : rattache le SousTypeProduit 
     * au TypeProduit UNE SEULE FOIS
     * via le setter canonique (idempotent) pour stabiliser le graphe.</li>
     * <li>CONVERTIT les Enfants Produit de la liste produits.</li>
     * <li>passe la liste des produits convertis au sousTypeProduit
     * via le setter canonique de SousTypeProduit.</li>
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
     * @param pSousTypeProduitJPA : SousTypeProduitJPA :
     * Entity JPA correspondant à l'objet métier SousTypeProduit.
     * @param ctx : ConversionContext
     *
     * @return SousTypeProduit : objet métier associé à l'Entity JPA.
     */
    private static SousTypeProduit sousTypeProduitJPAToMetier(
            final SousTypeProduitJPA pSousTypeProduitJPA,
            final ConversionContext ctx) {

        /* ne fait rien et retourne null si pSousTypeProduitJPA == null. */
        if (pSousTypeProduitJPA == null) {
            return null;
        }

        /* ne fait rien et retourne null si
         * pSousTypeProduitJPA.getSousTypeProduit() est blank
         * (null ou espaces). */
        if (StringUtils.isBlank(
                pSousTypeProduitJPA.getSousTypeProduit())) {
            return null;
        }

        /* retourne l'objet métier s'il était déjà dans le cache. */
        final SousTypeProduit cached = ctx.get(pSousTypeProduitJPA);
        if (cached != null) {
            return cached;
        }

        /* instancie un nouveau SousTypeProduit. */
        final SousTypeProduit sousTypeProduit
            = new SousTypeProduit();

        /* met le nouveau SousTypeProduit dans le cache. */
        ctx.put(pSousTypeProduitJPA, sousTypeProduit);

        /* passe les paramètres scalaires au nouveau SousTypeProduit. */
        sousTypeProduit.setIdSousTypeProduit(
                pSousTypeProduitJPA.getIdSousTypeProduit());

        /* IMPORTANT : nommer avant rattachement. */
        sousTypeProduit.setSousTypeProduit(
                pSousTypeProduitJPA.getSousTypeProduit());

        /* CONVERSION du Parent TypeProduit 
         * (via la présente interface)*** */
        final TypeProduitI typeProduitDansJPA
            = pSousTypeProduitJPA.getTypeProduit();

        final TypeProduitJPA tpJPA = requireJPA(
                typeProduitDansJPA
                , TypeProduitJPA.class
                , "SousTypeProduitJPA.typeProduit (parent)");

        /* IMPORTANT : conversion du parent
         * pour stabiliser le graphe et rattacher.*/
        final TypeProduitI tpMetier = typeProduitJPAToMetier(tpJPA, ctx);

        if (tpMetier != null) {
            if (sousTypeProduit.getTypeProduit() != tpMetier) {
                sousTypeProduit.setTypeProduit(tpMetier);
            }
        }

        /* IMPORTANT :
         * rattachement effectué ici via le setter canonique (idempotent),
         * afin de stabiliser le graphe. */

        // CONVERSION des Enfants Produits.************
        /* on stabilise le cache, MAIS on ne rattache PAS via
         * le Setter canonique sousTypeProduit.setProduits(...)
         * car le rattachement est fait UNE SEULE FOIS côté enfant
         * dans produit.setSousTypeProduit(...).*/
        final List<? extends ProduitI> produitsJPA
            = pSousTypeProduitJPA.getProduits();

        if (produitsJPA != null) {

            for (final ProduitI produitJPA : produitsJPA) {

                if (produitJPA == null) {
                    continue;
                }

                final ProduitJPA pJPA = requireJPA(
                        produitJPA
                        , ProduitJPA.class
                        , "SousTypeProduitJPA.produits -> élément");

                /* Convertit et rattache via le setter canonique
                 * du Produit (produit.setSousTypeProduit(stpMetier)) */
                produitJPAToMetier(pJPA, ctx);
            }

        } else {

            /* LAZY-SAFE :
             * NE PAS "wiper" les collections métier si la collection JPA
             * est null / non chargée.
             * (conversion depuis enfant/leaf peut avoir 
             * déjà stabilisé le graphe)
             */
            /* ne pas faire : sousTypeProduit.setProduits(null); */
        }

        return sousTypeProduit;

    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * transforme une Entity JPA
     * <code style="font-weight:bold;">ProduitJPA</code>
     * en objet métier
     * <code style="font-weight:bold;">Produit</code></p>
     * <ul>
     * <li>ne fait rien et retourne null si pProduitJPA == null.</li>
     * <li>ne fait rien et retourne null si
     * pProduitJPA.getProduit() est blank (null ou espaces).</li>
     * <li>retourne l'objet métier s'il était déjà dans le cache.</li>
     * <li>instancie un nouveau Produit.</li>
     * <li>met le nouveau Produit dans le cache.</li>
     * <li>passe les paramètres scalaires au nouveau Produit.</li>
     * <li>CONVERTIT le Parent SousTypeProduit
     * (via la présente interface).</li>
     * <li>passe le parent SousTypeProduit converti 
     * au Produit converti
     * via le setter canonique de Produit.</li>
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
     * @param pProduitJPA :
     * Entity JPA correspondant à l'objet métier Produit.
     * @param ctx : ConversionContext
     *
     * @return Produit : objet métier associé à l'Entity JPA.
     */
    private static Produit produitJPAToMetier(
            final ProduitJPA pProduitJPA
            , final ConversionContext ctx) {

        /* ne fait rien et retourne null si pProduitJPA == null. */
        if (pProduitJPA == null) {
            return null;
        }

        /* ne fait rien et retourne null si
         * pProduitJPA.getProduit() est blank (null ou espaces). */
        if (StringUtils.isBlank(pProduitJPA.getProduit())) {
            return null;
        }

        /* retourne l'objet métier s'il était déjà dans le cache. */
        final Produit cached = ctx.get(pProduitJPA);
        if (cached != null) {
            return cached;
        }

        /* instancie un nouveau Produit. */
        final Produit produit = new Produit();

        /* met le nouveau Produit dans le cache. */
        ctx.put(pProduitJPA, produit);

        /* passe les paramètres scalaires au nouveau Produit. */
        produit.setIdProduit(pProduitJPA.getIdProduit());
        produit.setProduit(pProduitJPA.getProduit());

        /* CONVERTIT le Parent SousTypeProduit 
         * (via la présente interface). */
        final SousTypeProduitJPA stpJPA = requireJPA(
                pProduitJPA.getSousTypeProduit()
                , SousTypeProduitJPA.class
                , "ProduitJPA.sousTypeProduit (parent)");

        final SousTypeProduitI stpMetier
            = sousTypeProduitJPAToMetier(stpJPA, ctx);

        /* passe le parent SousTypeProduit converti 
         * au Produit converti
         * via le setter canonique de Produit. */
        if (produit.getSousTypeProduit() != stpMetier) {
            produit.setSousTypeProduit(stpMetier);
        }

        return produit;
    }

    
    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Définit une <span style="font-weight:bold;">
     * "égalité métier"</span> entre une Entity JPA
     * <code style="font-weight:bold;">TypeProduitJPA</code>
     * et un objet métier
     * <code style="font-weight:bold;">TypeProduit</code>.</p>
     * <p>l'égalité métier suppose de respecter au minimum
     * le contrat Java de equals(), à savoir que :
     * <ul>
     * <li>les typeProduit (String) sont les mêmes dans
     * l'Entity JPA et dans l'objet métier.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">pTypeProduitJPA
     * et pTypeProduit sont equalsMetier si : </p>
     * <ul>
     * <li style="font-weight:bold;">idTypeProduitJPA == idTypeProduit</li>
     * <li style="font-weight:bold;">typeProduitJPA == typeProduit (String)</li>
     * </ul>
     * </div>
     *
     * <div>
     * <ul>
     * <li>retourne true si les deux paramètres sont null.</li>
     * <li>retourne false si un seul des 2 paramètres est null.</li>
     * <li>retourne false si les idTypeProduit ne sont pas égaux.</li>
     * <li>retourne typeProduitJPA.equals(typeProduit)
     * les idTypeProduit étant identiques.</li>
     * </ul>
     * </div>
     *
     * @param pTypeProduitJPA : TypeProduitJPA
     * @param pTypeProduit : TypeProduitI
     * @return boolean : true si equalsMetier
     */
    public static boolean equalsMetier(
            final TypeProduitJPA pTypeProduitJPA
            , final TypeProduitI pTypeProduit) {

        /* retourne true si les deux paramètres sont null.*/
        /* retourne false si un seul des 2 paramètres est null. */
        if (pTypeProduitJPA == null) {
            return pTypeProduit == null;
        }

        if (pTypeProduit == null) {
            return false;
        }

        /* idTypeProduit. */
        /* retourne false si les idTypeProduit ne sont pas égaux. */
        if (!Objects.equals(pTypeProduitJPA.getIdTypeProduit()
                , pTypeProduit.getIdTypeProduit())) {
            return false;
        }

        /* typeProduit. */
        /* retourne typeProduitJPA.equals(typeProduit).*/
        return Strings.CI.equals(
                pTypeProduitJPA.getTypeProduit()
                , pTypeProduit.getTypeProduit());

    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Définit une <span style="font-weight:bold;">
     * "égalité métier"</span> entre une Entity JPA
     * <code style="font-weight:bold;">SousTypeProduitJPA</code>
     * et un objet métier
     * <code style="font-weight:bold;">SousTypeProduit</code>.</p>
     * <p>l'égalité métier suppose de respecter au minimum
     * le contrat Java de equals(), à savoir que :
     * <ul>
     * <li>les typeProduit du SousTypeProduit
     * doivent être "equalsMetier" dans l'Entity JPA
     * et dans l'objet métier.</li>
     * <li>les sousTypeProduit (String) sont les mêmes dans
     * l'Entity JPA et dans l'objet métier.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">pSousTypeProduitJPA.equalsMetier(
     * pSousTypeProduit) si :</p>
     * <ul>
     * <li style="font-weight:bold;">
     * idSousTypeProduitJPA == idSousTypeProduit</li>
     * <li style="font-weight:bold;">
     * typeProduitJPA.equalsMetier(typeProduit)</li>
     * <li style="font-weight:bold;">
     * soustypeProduitStringJPA.equals(soustypeProduitString)</li>
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
     * soustypeProduitStringJPA et soustypeProduitString
     * (les idSousTypeProduit et TypeProduit étant equals par ailleurs).</li>
     * </ul>
     * </div>
     *
     * @param pSousTypeProduitJPA : SousTypeProduitJPA
     * @param pSousTypeProduit : SousTypeProduit
     *
     * @return boolean : true si equalsMetier.
     */
    public static boolean equalsMetier(
            final SousTypeProduitJPA pSousTypeProduitJPA
            , final SousTypeProduitI pSousTypeProduit) {

        /* retourne true si les deux paramètres sont null.*/
        /* retourne false si un seul des 2 paramètres est null.*/
        if (pSousTypeProduitJPA == null) {
            return pSousTypeProduit == null;
        }
        if (pSousTypeProduit == null) {
            return false;
        }

        /* idSousTypeProduit. */
        /* retourne false si les idSousTypeProduitJPA ne sont pas égaux. */
        if (!Objects.equals(
                pSousTypeProduitJPA.getIdSousTypeProduit()
                , pSousTypeProduit.getIdSousTypeProduit())) {
            return false;
        }

        /* TypeProduit. */
        /* retourne false si les deux TypeProduit
         * ne sont pas equalsMetier. */
        final TypeProduitI typeProduit = pSousTypeProduit.getTypeProduit();
        final TypeProduitJPA typeProduitJPA
        = (pSousTypeProduitJPA.getTypeProduit()
                instanceof TypeProduitJPA tp) ? tp : null;

        if (!equalsMetier(typeProduitJPA, typeProduit)) {
            return false;
        }

        /* sousTypeProduit. */
        /* retourne la comparaison entre les String
         * soustypeProduitStringJPA et soustypeProduitString. */
        return Strings.CI.equals(
                pSousTypeProduitJPA.getSousTypeProduit()
                , pSousTypeProduit.getSousTypeProduit());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Définit une <span style="font-weight:bold;">
     * "égalité métier"</span> entre une Entity JPA
     * <code style="font-weight:bold;">ProduitJPA</code>
     * et un objet métier
     * <code style="font-weight:bold;">Produit</code>.</p>
     * <p>l'égalité métier suppose de respecter au minimum
     * le contrat Java de equals(), à savoir que :
     * <ul>
     * <li>les SousTypeProduit du TypeProduit
     * doivent être "equalsMetier" dans l'Entity JPA
     * et dans l'objet métier.</li>
     * <li>les Produit (String) sont les mêmes dans
     * l'Entity JPA et dans l'objet métier.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">
     * pProduitJPA.equalsMetier(pProduit) si :</p>
     * <ul>
     * <li style="font-weight:bold;">
     * idProduitJPA == idProduit</li>
     * <li style="font-weight:bold;">
     * sousTypeProduitJPA.equalsMetier(sousTypeProduit)</li>
     * <li style="font-weight:bold;">
     * produitStringJPA.equals(produitString)</li>
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
     * produitStringJPA et produitString
     * (les idProduit et SousTypeProduit étant equals par ailleurs).</li>
     * </ul>
     * </div>
     *
     * @param pProduitJPA : ProduitJPA
     * @param pProduit : Produit
     * @return true si equalsMetier
     */
    public static boolean equalsMetier(
            final ProduitJPA pProduitJPA
            , final ProduitI pProduit) {

        /* retourne true si les deux paramètres sont null.*/
        /* retourne false si un seul des 2 paramètres est null.*/
        if (pProduitJPA == null) {
            return pProduit == null;
        }
        if (pProduit == null) {
            return false;
        }

        /* idProduit. */
        /* retourne false si les idProduit ne sont pas égaux. */
        if (!Objects.equals(
                pProduitJPA.getIdProduit(), 
                pProduit.getIdProduit())) {
            return false;
        }

        /* SousTypeProduit. */
        /* retourne false si les deux SousTypeProduit
         * ne sont pas equalsMetier. */
        final SousTypeProduitI stp = pProduit.getSousTypeProduit();
        final SousTypeProduitJPA stpJPA
        = (pProduitJPA.getSousTypeProduit()
                instanceof SousTypeProduitJPA stp2) ? stp2 : null;

        if (!equalsMetier(stpJPA, stp)) {
            return false;
        }

        /* produit (String). */
        /* retourne la comparaison entre les String produitString. */
        return Strings.CI.equals(
                pProduitJPA.getProduit(), 
                pProduit.getProduit());
    }

    
    
    /**
     *
     * @param <T> : T
     * @param o : Object
     * @param expected : Class<T>
     * @param contexte String
     * @return T
     */
    private static <T> T requireJPA(
            final Object o
            , final Class<T> expected
            , final String contexte) {

        if (o == null) {
            throw new IllegalStateException(contexte + " : objet null");
        }

        if (!expected.isInstance(o)) {
            throw new IllegalStateException(
                IMPLEMENTATION_NON_JPA + "attendue = " 
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

        final String pres1
            = String.format("[idTypeProduit : "
                    + FORMAT_ID
                    + TIRET_ESPACE
                    + "typeProduit"
                    + DEUX_POINTS_ESPACE
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

                /* Cast par pattern matching (canonique + sans cast risqué)*/
                final TypeProduitI tpI = sousTypeProduitJPA.getTypeProduit();
                final TypeProduitJPA typeProduitJPAduSousTypeProduit =
                        (tpI instanceof TypeProduitJPA other) ? other : null;

                final List<? extends ProduitI> produitsDansSousProduit
                    = sousTypeProduitJPA.getProduits();

                Long idTypeProduitJPAduSousTypeProduit = null;
                String typeProduitJPAduSousTypeProduitString = null;

                if (typeProduitJPAduSousTypeProduit != null) {
                    idTypeProduitJPAduSousTypeProduit
                        = typeProduitJPAduSousTypeProduit.getIdTypeProduit();
                    typeProduitJPAduSousTypeProduitString
                        = typeProduitJPAduSousTypeProduit.getTypeProduit();
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
                        , idTypeProduitJPAduSousTypeProduit
                        , typeProduitJPAduSousTypeProduitString);

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
                        final String produitJPAString 
                        	= produitJPA.getProduit();

                        /*Cast par pattern matching 
                         * (canonique + sans cast risqué)*/
                        final SousTypeProduitI stpI 
                        	= produitJPA.getSousTypeProduit();
                        final SousTypeProduitJPA sousTypeProduitJPAProduit =
                                (stpI instanceof SousTypeProduitJPA other) 
                                ? other : null;

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
     * retourne une String formatée pour l'affichage
     * d'un TypeProduit</p>
     * <ul>
     * <li>retourne null si pTypeProduit == null.</li>
     * <li>affiche le TypeProduit</li>
     * <li>affiche la liste des SousTypeProduit contenus
     * dans le TypeProduit</li>
     * <li>affiche pour chaque SousTypeProduit 
     * la liste des Produit qu'il contient.</li>
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
     * @param pTypeProduit : TypeProduit : Objet métier à afficher
     * @return String
     */
    public static String afficherTypeProduitFormate(
            final TypeProduit pTypeProduit) {

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
                    + "typeProduit : "
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

                /* Cast par pattern matching (canonique + sans cast risqué) */
                final TypeProduitI tpSTPi = sousTypeProduit.getTypeProduit();
                final TypeProduit typeProduitduSousTypeProduit =
                        (tpSTPi instanceof TypeProduit other) ? other : null;

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
                        final String produitString = produit.getProduit();
                        final SousTypeProduitI sousTypeProduitProduit
                            = produit.getSousTypeProduit();

                        String sousTypeProduitProduitString = null;

                        if (sousTypeProduitProduit != null) {
                            sousTypeProduitProduitString
                                = sousTypeProduitProduit.getSousTypeProduit();
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
                                , idProduit
                                , produitString
                                , sousTypeProduitProduitString);

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
                final SousTypeProduit sousTypeProduit
                    = (SousTypeProduit) produit.getSousTypeProduit();

                /* sousTypeProduit. */
                Long idSousProduit = null;
                String sousTypeProduitString = null;
                TypeProduit typeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;

                if (sousTypeProduit != null) {
                    idSousProduit
                        = sousTypeProduit.getIdSousTypeProduit();
                    sousTypeProduitString
                        = sousTypeProduit.getSousTypeProduit();
                    typeProduitduSousTypeProduit
                        = (TypeProduit) sousTypeProduit.getTypeProduit();
                    if (typeProduitduSousTypeProduit != null) {
                        typeProduitduSousTypeProduitString
                            = typeProduitduSousTypeProduit.getTypeProduit();
                    }
                }

                /* TYPEPRODUIT*/
                final TypeProduit typeProduit 
                	= (TypeProduit) produit.getTypeProduit();

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
