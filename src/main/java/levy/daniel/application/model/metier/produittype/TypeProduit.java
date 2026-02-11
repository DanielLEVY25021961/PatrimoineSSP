/* ********************************************************************* */
/* ************************* OBJET METIER ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.metier.produittype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <div>
 * <style>p, ul, li {line-height : 1em;}</style>
 * <p style="font-weight:bold;">CLASSE TypeProduit :</p>
 * <p>modélise un <span style="font-weight:bold;">type de produit</span> 
 * comme "vêtement", "outillage", "logiciel"...</p>
 * </div>
 * 
 * <div>
 * 
 * <div>
 * <p>Dans ce modèle de PRODUIT TYPE, un TypeProduit 
 * comme "vêtement" se décline en SousTypeProduit comme :</p>
 * <ul>
 * <li>"vêtement pour homme"</li>
 * <li>"vêtement pour femme"</li>
 * <li>"vêtement pour enfant"</li>
 * </ul>
 * </div>
 * <div>
 * <p>un SousTypeProduit comme "vêtement pour homme" qualifie des Produit comme : </p>
 * <ul>
 * <li>"chemise à manches longues pour homme"</li>
 * <li>"chemise à manches courtes pour homme"</li>
 * <li>"tee-shirt pour homme"</li>
 * </ul>
 * <p>Il y a donc 3 Classes : 
 * <ol>
 * <li>TypeProduit,</li> 
 * <li>SousTypeProduit,</li> 
 * <li>Produit</li>
 * </ol>
 * pour définir une PRODUIT TYPE</p>
 * </div>
 * 
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
 * Diagramme de Classes du Produit qualifié par un SousTypeProduit qui est lui-même une déclinaison d'un TypeProduit</p>
 * <p>
 * <img src="../../../../../../../../../javadoc/images/model/metier/produittype/diagramme_de_classes_produit_typé.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 300px />
 * </p>
 * </div>
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;padding : 0px;">Exemple d'utilisation : </p>
 * <p><code>// Instanciation d'un TypeProduit</code></p>
 * <p><code>final TypeProduit typeProduitVetement = new TypeProduit("vêtement");</code></p>
 * <p><code>// Instanciation d'un SousTypeProduit (qui a pour TypeProduit typeProduitVetement)</code></p>
 * <p><code>final SousTypeProduit sousTypeProduitVetementPourHomme = new SousTypeProduit("vêtement pour homme", typeProduitVetement, null);</code></p>
 * <p><code>// Instanciation d'un Produit (qui a pour SousTypeProduit sousTypeProduitVetementPourHomme)</code></p>
 * <p><code>final Produit produitChemisePourHomme = new Produit("chemise manches longues pour homme", sousTypeProduitVetementPourHomme);</code></p>
 * </div>
 * 
 * </div>
 * 
 * @author Daniel LEVY
 * @version 1.0
 * @created 06 décembre 2025 20:59:31
 */
public class TypeProduit implements TypeProduitI, Cloneable {

	/* ------------------------ CONSTANTES ----------------------------- */
	
	/**
	 * <div>
	 * <p>"null"</p>
	 * </div>
	 */
	public static final String NULL = "null";
	
	/**
	 * <div>
	 * <p>", "</p>
	 * </div>
	 */
	public static final String VIRGULE_ESPACE = ", ";
	
	/**
	 * <div>
	 * <p>']'</p>
	 * </div>
	 */
	public static final char CROCHET_FERMANT = ']';
		
	/**
	 * <div>
	 * <p>';'</p>
	 * </div>
	 */
	public static final char POINT_VIRGULE = ';';
		
	/**
	 * <div>
	 * <p>System.getProperty("line.separator")</p>
	 * </div>
	 */
	public static final String SAUT_DE_LIGNE 
		= System.getProperty("line.separator");
	
	/**
	 * <div>
	 * <p>"le SousTypeProduit passé en paramètre 
	 * n'est pas de type Objet métier : "</p>
	 * </div>
	 */
	public static final String MAUVAISE_INSTANCE_METIER 
		= "le SousTypeProduit passé en paramètre "
				+ "n'est pas de type Objet métier : ";
	
	/**
	 * <div>
	 * <p>"le SousTypeProduit passé en paramètre 
	 * n'est pas de type Objet métier : "</p>
	 * </div>
	 */
	public static final String MAUVAISE_INSTANCE_ENFANT_METIER 
		= "le SousTypeProduit passé en paramètre "
				+ "n'est pas de type Objet métier : ";
	
	/**
	 * <div>
	 * <p>"******* TypeProduit : "</p>
	 * </div>
	 */
	public static final String EN_TETE_TYPE_PRODUIT 
		= "******* TypeProduit : ";

	 /**
	 * <div>
	 * <p>"******* sousTypeProduits du TypeProduit : "</p>
	 * </div>
	 */
	public static final String EN_TETE_SOUS_TYPES_PRODUIT 
		= "******* sousTypeProduits du TypeProduit : ";

	 /**
	 * <div>
	 * <p>"***** liste des produits dans le sousProduit : "</p>
	 * </div>
	 */
	public static final String EN_TETE_LISTE_PRODUITS 
		= "***** liste des produits dans le sousProduit : ";

	 /**
	 * <div>
	 * <p>"[idTypeProduit : %-2s - typeProduit : %-10s]"</p>
	 * </div>
	 */
	public static final String FORMAT_ID_TYPE_PRODUIT 
		= "[idTypeProduit : %-2s - typeProduit : %-10s]";

	 /**
	 * <div>
	 * <p>"[idSousTypeProduit : 
	 * %-2s
	 *  - sousTypeProduit : 
	 *  %-20s 
	 * - [idTypeProduit du TypeProduit dans le SousTypeProduit : %-2s 
	 * - typeProduitString du TypeProduit dans le SousTypeProduit : %-13s]]"</p>
	 * </div>
	 */
	public static final String FORMAT_SOUS_TYPE_PRODUIT 
		= "[idSousTypeProduit : "
				+ "%-2s"
				+ " - sousTypeProduit : "
				+ "%-20s "
				+ "- [idTypeProduit du TypeProduit dans le SousTypeProduit : "
				+ "%-2s "
				+ "- typeProduitString du TypeProduit dans le SousTypeProduit : "
				+ "%-13s]]";

	 /**
	 * <div>
	 * <p>"[idProduit dans produits du SousTypeProduit : 
	 * %-2s
	 *  - produit dans produits du SousTypeProduit : 
	 *  %-40s
	 *   - sousTypeProduit dans le produit : 
	 *   %-20s]"</p>
	 * </div>
	 */
	public static final String FORMAT_PRODUIT 
		= "[idProduit dans produits du SousTypeProduit : "
				+ "%-2s"
				+ " - produit dans produits du SousTypeProduit : "
				+ "%-40s"
				+ " - sousTypeProduit dans le produit : "
				+ "%-20s]";


	// ************************ATTRIBUTS**********************************/
	
	/**
	 * <div>
	 * <p>ID en base du type de produit.</p>
	 * </div>
	 */
	private Long idTypeProduit;
 
	/**
	 * <div>
	 * <p>type de produit comme "vêtement", "outillage", ...</p>
	 * </div>
	 */
	private String typeProduit;
	
	/**
	 * <div>
	 * <p>Liste des sous-types de produit du présent type de produit.</p>
	 * <p>par exemple, pour le type de produit "vêtement" :</p>
	 * <ul>
	 * <li>vêtement pour homme</li>
	 * <li>vêtement pour femme</li>
	 * <li>vêtement pour enfant</li>
	 * </ul>
	 * <p>ATTENTION : visibilité interface.</p>
	 * </div>
	 */
	private List<SousTypeProduitI> sousTypeProduits 
			= new ArrayList<SousTypeProduitI>();

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
	private static final Logger LOG 
		= LogManager.getLogger(TypeProduit.class);
	
 // ====================== METHODES ======================================
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduit() {
        this(null, null, null);
    }

	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * </div>
	 *
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 */
	public TypeProduit(final String pTypeProduit) {
        this(null, pTypeProduit, null);
    }
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2 UTILE avec id et typeProduit.</p>
	 * </div>
	 *
	 * @param pIdTypeProduit : Long : ID en base du type de produit
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 */
	public TypeProduit(
			final Long pIdTypeProduit,
			final String pTypeProduit) {
		this(pIdTypeProduit, pTypeProduit, null);
	}
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 * @param pSousTypeProduits : List&lt;SousTypeProduitI&gt; : 
	 * Liste des sous-types de produit du présent type de produit.
	 * ATTENTION : visibilité Interface.
	 */
	public TypeProduit(
			final String pTypeProduit,
			final List<SousTypeProduitI> pSousTypeProduits) {
		this(null, pTypeProduit, pSousTypeProduits);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR COMPLET.</p>
	 * <p>ATTENTION : avec cette implémentation, 
	 * la liste <code>this.sousTypeProduits</code> 
	 * n'est jamais null (mais vide).</p>
	 * </div>
	 * 
	 *
	 * @param pIdTypeProduit : Long : ID en base du type de produit
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 * @param pSousTypeProduits : List&lt;SousTypeProduitI&gt; : 
	 * Liste des sous-types de produit du présent type de produit.
	 * ATTENTION : visibilité Interface.
	 */
	public TypeProduit(final Long pIdTypeProduit
			, final String pTypeProduit
			, final List<SousTypeProduitI> pSousTypeProduits) {

		super();
		this.idTypeProduit = pIdTypeProduit;
		this.typeProduit = pTypeProduit;

		// Canonique : on ne garde jamais une référence externe
		// directe si on veut maîtriser la cohérence.
		this.sousTypeProduits = new ArrayList<>();

		if (pSousTypeProduits != null) {

			for (final SousTypeProduitI stp : pSousTypeProduits) {
				this.rattacherEnfantSTP(stp);
			}
		}
	}
	

	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">hashCode() sur :</p>
	 * <ol>
	 * <li style="font-weight:bold;">typeProduit</li>
	 * </ol>
	 * </div>
	 */
	@Override
	public final int hashCode() {
		synchronized (this) {
			return Objects.hash(this.typeProduit);
		}	    
	}


	
	/**
	 * {@inheritDoc}
	 * Compare deux TypeProduit de manière thread-safe.
	 * <div>
	 * <p>
	 * Cette méthode utilise une double synchronisation 
	 * avec un ordre de verrouillage
	 * basé sur {@code System.identityHashCode()} pour éviter les deadlocks.
	 * </p>
	 * </div>
	 * <div>
	 * <p style="font-weight:bold;">equals() sur :</p>
	 * <ol>
	 * <li style="font-weight:bold;">typeProduit</li>
	 * </ol>
	 * </div>
	 */
	@Override
	public final boolean equals(final Object pObject) {

		/*
		 * retourne true si les références sont identiques.
		 */
		if (this == pObject) {
			return true;
		}

		/*
		 * return false si pObject == null.
		 */
		if (pObject == null) {
			return false;
		}

		/*
		 * retourne false si pObject
		 * n'est pas une bonne instance.
		 */
		if (!(pObject instanceof TypeProduit other)) {
			return false;
		}

		/*
		 * equals sur [TypeProduit].
		 */
		synchronized (this) {
			return Objects.equals(
					this.typeProduit, other.typeProduit);
		}
	}	
	

	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final String toString() {		
		/*
	     * Génère une représentation textuelle thread-safe.
	     */
		final StringBuilder builder = new StringBuilder();
		
		synchronized (this) {
			
			builder.append("TypeProduit [");

			builder.append("idTypeProduit=");
				
			if (this.idTypeProduit != null) {
				builder.append(this.idTypeProduit);
			} else {
				builder.append(NULL);
			}
			
			builder.append(VIRGULE_ESPACE);

			builder.append("typeProduit=");
			
			if (this.typeProduit != null) {			
					builder.append(this.typeProduit);			
			} else {
				builder.append(NULL);
			}

			builder.append(CROCHET_FERMANT);
		}
				
		return builder.toString();
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final String afficherTypeProduit() {
		
		if (this.getTypeProduit() == null) {
			return NULL;
		}
		
		return this.getTypeProduit();
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String afficherTypeProduitFormate(
			final TypeProduitI pTypeProduit) {

	    /* retourne null si pTypeProduit == null. */
	    if (pTypeProduit == null) {
	        return null;
	    }

	    final Long idTypeProduitProv = pTypeProduit.getIdTypeProduit();
	    final String typeProduitString = pTypeProduit.getTypeProduit();
	    final List<? extends SousTypeProduitI> sousTypeProduitsProv 
	    	= pTypeProduit.getSousTypeProduits();

	    final StringBuilder stb = new StringBuilder();

	    /*
	     * Construction de l'en-tête du TypeProduit.
	     */
	    stb.append(EN_TETE_TYPE_PRODUIT);
	    stb.append(typeProduitString);
	    stb.append(" *******");
	    stb.append(SAUT_DE_LIGNE);

	    final String pres1 = String.format(
	    		FORMAT_ID_TYPE_PRODUIT
	    			, idTypeProduitProv
	    				, typeProduitString);
	    
	    stb.append(pres1);
	    stb.append(SAUT_DE_LIGNE);
	    stb.append(SAUT_DE_LIGNE);

	    /*
	     * Affichage des sous-types de produit.
	     */
	    stb.append(EN_TETE_SOUS_TYPES_PRODUIT);
	    stb.append(typeProduitString);
	    stb.append(SAUT_DE_LIGNE);

	    if (sousTypeProduitsProv == null) {
	        stb.append(NULL);
	    } else if (sousTypeProduitsProv.isEmpty()) {
	        stb.append("vide");
	    } else {
	    	
	        for (final SousTypeProduitI sousTypeProduit : sousTypeProduitsProv) {
	            final Long idSousTypeProduit 
	            	= sousTypeProduit.getIdSousTypeProduit();
	            final String sousTypeProduitString 
	            	= sousTypeProduit.getSousTypeProduit();
	            final TypeProduitI typeProduitduSousTypeProduit 
	            	= sousTypeProduit.getTypeProduit();

	            Long idTypeProduitduSousTypeProduit = null;
	            String typeProduitduSousTypeProduitString = null;

	            if (typeProduitduSousTypeProduit != null) {
	                idTypeProduitduSousTypeProduit 
	                	= typeProduitduSousTypeProduit.getIdTypeProduit();
	                typeProduitduSousTypeProduitString 
	                	= typeProduitduSousTypeProduit.getTypeProduit();
	            }

	            final String pres2 = String.format(
	                FORMAT_SOUS_TYPE_PRODUIT,
	                idSousTypeProduit
	                , sousTypeProduitString
	                , idTypeProduitduSousTypeProduit
	                , typeProduitduSousTypeProduitString
	            );

	            stb.append(pres2);
	            stb.append(SAUT_DE_LIGNE);

	            /*
	             * Affichage des produits dans le sous-type de produit.
	             */
	            stb.append(EN_TETE_LISTE_PRODUITS);
	            stb.append(sousTypeProduitString);
	            stb.append(SAUT_DE_LIGNE);

	            final List<? extends ProduitI> produitsDansSousProduit 
	            = sousTypeProduit.getProduits();
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

	                    final String presProduit = String.format(
	                        FORMAT_PRODUIT,
	                        idProduit, produitString
	                        , sousTypeProduitProduitString
	                    );

	                    stb.append('\t');
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
	* {@inheritDoc}
	*/
	@Override
	public final String afficherSousTypeProduits() {
		
		if (this.sousTypeProduits == null) {
			return NULL;
		}
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append("Sous-Types de Produit=[");
		
		builder.append(
			    this.sousTypeProduits.stream()
			        .map(SousTypeProduitI::afficherSousTypeProduit)
			        .collect(Collectors.joining(VIRGULE_ESPACE))
			);
		
		builder.append(CROCHET_FERMANT);
		
		return builder.toString();
	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <ol>
	 * <p style="font-weight:bold;">Classe dans l'ordre alphabétique de :</p>
	 * <li style="font-weight:bold;">typeProduit</li>
	 * </ol>
	 * <p style="font-weight:bold;">ATTENTION : 
	 * Strings.CI.compare(a, b) place les vides avant tout texte.</p>
	 * </div>
	 */
	@Override
	public final int compareTo(final TypeProduitI pObject) {

	    /* Comparaison de la même instance retourne toujours 0. */
	    if (this == pObject) {
	        return 0;
	    }

	    /* Comparaison avec null retourne toujours < 0. */
	    if (pObject == null) {
	        return -1;
	    }

	    /*
	     * Verrouillage systématique et cohérent :
	     * toujours this puis pObject.
	     * Convention globale du projet pour éviter les deadlocks.
	     */
	    synchronized (this) {
	        synchronized (pObject) {
	            return compareFields(pObject);
	        }
	    }
	}
	

	
	/**
	 * <p style="font-weight:bold;">
	 * Compare les champs de manière thread-safe en accédant 
	 * directement aux champs et pas aux getters 
	 * (pas toujours Thread-Safe).</p>
	 *
	 * @param pObject : TypeProduitI : 
	 * L'objet à comparer avec this.
	 * @return Le résultat de la comparaison.
	 */
	private int compareFields(final TypeProduitI pObject) {
	    /*
	     * Accès directs aux champs pour éviter les appels aux getters
	     * non synchronisés. Le cast est safe 
	     * car TypeProduitI est implémenté
	     * uniquement par TypeProduit.
	     */
	    final String a = this.typeProduit;
	    
	    final String b;
	    if (pObject instanceof TypeProduit other) {
	        b = other.typeProduit;
	    } else {
	        b = pObject.getTypeProduit();
	    }

	    /*
	     * Gestion des cas null :
	     * - Si a est null et b est null, les objets sont égaux (retourne 0).
	     * - Si a est null et b n'est pas null, 
	     * a est considéré comme "après" b (retourne +1).
	     * - Si a n'est pas null et b est null, 
	     * a est considéré comme "avant" b (retourne -1).
	     */
	    if (a == null) {
	        return (b == null) ? 0 : +1; /* null "après". */
	    }

	    if (b == null) {
	        return -1;
	    }

	    /*
	     * Comparaison case-insensitive des chaînes de caractères.
	     * Strings.CI.compare() place les chaînes vides avant les autres.
	     */
	    return Strings.CI.compare(a, b);
	}	


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final TypeProduit clone() throws CloneNotSupportedException {
		return this.cloneDeep();
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final TypeProduit cloneDeep() {	
		return deepClone(new CloneContext());	
	}

	
	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">
	 * Effectue un clonage profond thread-safe.</p>
	 * <p>Utilise un CloneContext pour gérer les cycles 
	 * et éviter les duplications.</p>
	 * <p>Clône récursivement les enfants (SousTypeProduitI).</p>
	 * </div>
	 */
	@Override
	public final TypeProduit deepClone(final CloneContext ctx) {
	    /*
	     * Vérifie si un clone de cet objet existe déjà dans le contexte.
	     * Si oui, le retourne pour éviter les duplications.
	     */
	    final TypeProduit existing = ctx.get(this);
	    if (existing != null) {
	        return existing;
	    }

	    /*
	     * Crée un clone "nu" (sans enfants) de manière thread-safe.
	     * Le clone est ajouté au contexte avant de cloner les enfants
	     * pour gérer les références circulaires.
	     */
	    final TypeProduit clone;
	    synchronized (this) {
	        clone = this.cloneWithoutChildren();
	        ctx.put(this, clone);
	    }

	    /*
	     * Crée une copie thread-safe de la liste des enfants
	     * pour éviter les modifications concurrentes.
	     */
	    final List<? extends SousTypeProduitI> enfantsSafeCopy;
	    
	    synchronized (this) {
	        enfantsSafeCopy = new ArrayList<>(this.sousTypeProduits);
	    }

	    /*
	     * Clone chaque enfant de manière thread-safe.
	     * Chaque enfant est verrouillé individuellement 
	     * pendant son clonage.
	     */
	    for (final SousTypeProduitI enfant : enfantsSafeCopy) {
	    	
	        if (enfant == null) {
	            continue;
	        }

	        /*
	         * Clone l'enfant en synchronisant sur celui-ci
	         * pour éviter les incohérences.
	         */
	        final SousTypeProduitI cloneEnfant;
	        
	        synchronized (enfant) {
	            cloneEnfant = enfant.deepClone(ctx);
	        }

	        /* rattache le clone profond de l'enfant au clone parent
	         * via la méthode Thread-Safe canonique du parent. */
	        clone.rattacherEnfantSTP(cloneEnfant);
	    }

	    return clone;
	}


	
	/**
	 * {@inheritDoc}
	 */
	 @Override
	public final TypeProduit cloneWithoutChildren() {

		final TypeProduit clone = new TypeProduit();
		
		/*
	     * Crée une nouvelle instance vide de TypeProduit.
	     * Les champs sont copiés de manière thread-safe
	     * depuis l'objet courant.
	     */
		synchronized (this) {
			
			clone.idTypeProduit = this.idTypeProduit;
			clone.typeProduit = this.typeProduit;
			clone.sousTypeProduits = new ArrayList<SousTypeProduitI>();
		}

		return clone;
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void rattacherEnfantSTP(
			final SousTypeProduitI pEnfant) {

		/* Traite le cas d'une mauvaise instance en paramètre. */
		this.traiterMauvaiseInstanceSousTypeProduit(pEnfant);

		/* retourne et ne fait rien si pEnfant == null
		 * ou si le libellé de pEnfant est blank (null ou espaces). */
		if (pEnfant == null 
				|| StringUtils.isBlank(pEnfant.getSousTypeProduit())) {
			return;
		}

		/* Ordre de verrouillage UNIQUE :
		 * toujours parent (this) puis enfant (pEnfant). */
		synchronized (this) {

			synchronized (pEnfant) {

				/* Rattache pEnfant au parent this si nécessaire. */
				this.rattacherSiNecessaire(pEnfant);
			}
		}
	}



	 /**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Rattache l'enfant {@code SousTypeProduitI} pEnfant
	 * au présent parent {@code TypeProduit} si ce n'est pas déjà fait.
	 * </p>
	 * <ul>
	 * <li>Récupère le parent de pEnfant.</li>
	 * <li>Ne fait rien si le parent de pEnfant est déjà this.</li>
	 * <li>Sinon, rattache pEnfant au présent parent this.</li>
	 * <li>Utilise le SETTER CANONIQUE INTELLIGENT 
	 * {@code setTypeProduit(this)}  de l'enfant 
	 * {@code SousTypeProduitI} pEnfant pour le rattachement.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pEnfant : {@code SousTypeProduitI} :
	 * enfant à rattacher au présent parent this.
	 */
	private void rattacherSiNecessaire(final SousTypeProduitI pEnfant) {
		
		/* Récupère le parent de pEnfant. */
	    final TypeProduitI parentDeEnfant = pEnfant.getTypeProduit();
	    
	    /* Rattache pEnfant au présent parent this. */
	    if (parentDeEnfant != this) {
	        pEnfant.setTypeProduit(this);
	    }
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void detacherEnfantSTP(
			final SousTypeProduitI pEnfant) {

		/* Traite le cas d'une mauvaise instance en paramètre. */
		this.traiterMauvaiseInstanceSousTypeProduit(pEnfant);

		/* retourne et ne fait rien si pEnfant == null
		 * ou si le libellé de pEnfant est blank (null ou espaces). */
		if (pEnfant == null 
				|| StringUtils.isBlank(pEnfant.getSousTypeProduit())) {
			return;
		}

		/* Ordre de verrouillage UNIQUE :
		 * toujours parent (this) puis enfant (pEnfant). */
		synchronized (this) {

			synchronized (pEnfant) {

				/* Détache pEnfant du parent this si nécessaire. */
				this.detacherSiNecessaire(pEnfant);
			}
		}
	}
	 
	
	
	 /**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Détache l'enfant {@code SousTypeProduitI} pEnfant
	 * du présent parent {@code TypeProduit} si ce n'est pas déjà fait.
	 * </p>
	 * <ul>
	 * <li>Récupère le parent de pEnfant.</li>
	 * <li>Ne fait rien si le parent de pEnfant n'est pas this.</li>
	 * <li>Sinon, détache pEnfant du présent parent this.</li>
	 * <li>Utilise le SETTER CANONIQUE INTELLIGENT 
	 * {@code setTypeProduit(null)}  de l'enfant 
	 * {@code SousTypeProduitI} pEnfant pour le détachement.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pEnfant : {@code SousTypeProduitI} :
	 * enfant à détacher du présent parent this.
	 */
	private void detacherSiNecessaire(final SousTypeProduitI pEnfant) {
		
		/* Récupère le parent de pEnfant. */
	    final TypeProduitI parentDeEnfant = pEnfant.getTypeProduit();
	    
	    /* Détache pEnfant du présent parent this. */
	    if (parentDeEnfant == this) {
	        pEnfant.setTypeProduit(null);
	    }
	}



	 /**
	  * <div>
	  * <p>Ajoute pSousTypeProduit directement (add)
	  * <span style="font-weight:bold;">sans synchronisation</span>
	  * dans la liste
	  * <code style="font-weight:bold;">this.sousTypeProduits</code>
	  * du présent parent TypeProduitI</p>
	  * <p>Méthode protected interne au Package
	  * (utilisée par SousTypeProduit pour éviter les boucles).</p>
	  * <ul>
	  * <li>Traite le cas d'une mauvaise instance en paramètre.</li>
	  * <li>Ne fait rien et return si pSousTypeProduit == null.</li>
	  * <li>Ne fait rien si 
	  * this.sousTypeProduits.contains(pSousTypeProduit).
	  * Donc jamais de doublons.</li>
	  * <li>Fait un ajout direct sans synchronisation par add()
	  * de pSousTypeProduit dans <code style="font-weight:bold;">
	  * this.sousTypeProduits</code>.</li>
	  * </ul>
	  * </div>
	  *
	  * @param pSousTypeProduit : SousTypeProduitI
	  */
	 protected final void internalAddSousTypeProduit(
	         final SousTypeProduitI pSousTypeProduit) {
	     /*
	      * Traite le cas d'une mauvaise instance en paramètre.
	      */
	     this.traiterMauvaiseInstanceSousTypeProduit(pSousTypeProduit);

	     /*
	      * Ne fait rien et return si pSousTypeProduit == null.
	      */
	     if (pSousTypeProduit == null) {
	         return;
	     }

	     /*
	      * Ne fait rien si 
	      * this.sousTypeProduits.contains(pSousTypeProduit).
	      * Donc jamais de doublons.
	      */
	     synchronized (this) {
	         if (!this.sousTypeProduits.contains(pSousTypeProduit)) {
	             /*
	              * Fait un ajout direct sans synchronisation par add()
	              * de pSousTypeProduit dans this.sousTypeProduits.
	              */
	             this.sousTypeProduits.add(pSousTypeProduit);
	         }
	     }
	 }



	 /**
	  * <div>
	  * <p>Retire pSousTypeProduit directement (remove)
	  * <span style="font-weight:bold;">sans synchronisation</span>
	  * dans la liste
	  * <code style="font-weight:bold;">this.sousTypeProduits</code>
	  * du présent parent TypeProduitI</p>
	  * <p>Méthode protected interne au Package
	  * (utilisée par SousTypeProduit pour éviter les boucles).</p>
	  * <ul>
	  * <li>Traite le cas d'une mauvaise instance en paramètre.</li>
	  * <li>Ne fait rien et return si pSousTypeProduit == null.</li>
	  * <li>Ne fait rien si la liste ne contient pas pSousTypeProduit.</li>
	  * <li>Fait un retrait direct sans synchronisation par remove()
	  * de pSousTypeProduit dans <code style="font-weight:bold;">
	  * this.sousTypeProduits</code>.</li>
	  * </ul>
	  * </div>
	  *
	  * @param pSousTypeProduit : SousTypeProduitI
	  */
	 protected final void internalRemoveSousTypeProduit(
	         final SousTypeProduitI pSousTypeProduit) {
	     /*
	      * Traite le cas d'une mauvaise instance en paramètre.
	      */
	     this.traiterMauvaiseInstanceSousTypeProduit(pSousTypeProduit);

	     /*
	      * Ne fait rien et return si pSousTypeProduit == null.
	      */
	     if (pSousTypeProduit == null) {
	         return;
	     }

	     /*
	      * Ne fait rien si la liste ne contient pas pSousTypeProduit.
	      * Fait un retrait direct sans synchronisation par remove().
	      */
	     synchronized (this) {
	         this.sousTypeProduits.remove(pSousTypeProduit);
	     }
	 }


	 
	/**
	 * <div>
	 * <p>retourne une chaine de caractères "nettoyée" 
	 * sans espaces superflus avant et après (trim()).</p>
	 * <ul>
	 * <li>ne fait rien et retourne null si pString est null.</li>
	 * <li>applique un trim() sur pString.</li>
	 * <li>retourne null si pString est vide.</li>
	 * <li>retourne la String trimée si elle est non vide.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pString : String : String à normaliser
	 * @return String : pString normalisé (trimé).
	 */
	private static String normalize(final String pString) {
	
		/* ne fait rien et retourne null si pString est null. */
		if (pString == null) {
			return null;
		}
		
		/* applique un trim() sur pString. */
		final String trime = pString.trim();
		
		/* retourne null si pString est vide. */
		/* retourne la String trimée si elle est non vide. */
		return trime.isEmpty() ? null : trime;
	
	}



	/**
	* {@inheritDoc}
	* <div>
	 * <p style="font-weight:bold;">retourne une String pour 
	 * afficher l'en-tête d'un TypeProduit en csv.</p>
	 * </div>
	 * 
	 * @return String : 
	 * "idTypeProduit;type de produit;"
	*/
	@Override
	public final String getEnTeteCsv() {
		return "idTypeProduit;type de produit;";	
	}



	/**
	* {@inheritDoc}
	* <div>
	 * <p style="font-weight:bold;">retourne une String pour 
	 * l'affichage d'un TYPE-PRODUIT 
	 * sous forme de csv avec des séparateurs point-virgule ';'.</p>
	 * <p>Le csv retourné respecte l'ordre indiqué dans l'en-tête : 
	 * "idTypeProduit;type de produit;"</p>
	 * <ul>
	 * <p>par exemple : </p>
	 * <li>idTypeProduit;type de produit;</li>
	 * <li>null;vêtement;</li>
	 * </ul>
	 * </div>  
	 *
	 * @return String : "idTypeProduit;type de produit;"
	*/
	@Override
	public final String toStringCsv() {
	    
	    final Long idSnapshot;
	    final String typeSnapshot;
	    
	    synchronized (this) {
	        idSnapshot = this.idTypeProduit;
	        typeSnapshot = this.typeProduit;
	    }
	    
	    final StringBuilder builder = new StringBuilder();

	    /*
	     * idTypeProduit
	     */
	    if (idSnapshot != null) {
	        builder.append(idSnapshot);
	    } else {
	        builder.append(NULL);
	    }

	    builder.append(POINT_VIRGULE);

	    /*
	     * type de produit
	     */
	    if (typeSnapshot == null) {
	        builder.append(NULL);
	    } else {
	        builder.append(typeSnapshot);
	    }

	    builder.append(POINT_VIRGULE);

	    return builder.toString();
	}



	/**
	* {@inheritDoc}
	* <div>
	 * <p style="font-weight:bold;">en-tête Jtable 
	 * pour un SousTypeProduit</b> :</p>
	 * <p>"idTypeProduit;type de produit;".</p>
	 * </div>
	*/
	@Override
	public final String getEnTeteColonne(final int pI) {

		String entete = null;

		switch (pI) {

		case 0:
			entete = "idTypeProduit";
			break;
			
		case 1:
			entete = "type de produit";
			break;
			
		default:
			entete = "invalide";
			break;

		} // Fin du Switch._________________________________

		return entete;

	} // Fin de getEnTeteColonne(...)._____________________________________



	/**
	* {@inheritDoc}
	*  <div>
	 * <p style="font-weight:bold;">ligne Jtable 
	 * pour un TypeProduit</b> :</p>
	 * <p>"idTypeProduit;type de produit;".</p>
	 * </div>
	*/
	@Override
	public final Object getValeurColonne(final int pI) {

	    final Long idSnapshot;
	    final String typeSnapshot;

	    synchronized (this) {
	        idSnapshot = this.idTypeProduit;
	        typeSnapshot = this.typeProduit;
	    }

	    Object valeur = null;

	    switch (pI) {

	        case 0:
	            if (idSnapshot != null) {
	                valeur = String.valueOf(idSnapshot);
	            }
	            break;

	        case 1:
	            if (typeSnapshot != null) {
	                valeur = typeSnapshot;
	            }
	            break;

	        default:
	            valeur = "invalide";
	            break;

	    } // Fin du Switch._________________________________

	    return valeur;

	} // Fin de getValeurColonne(...)._____________________________________



	/**
	* {@inheritDoc}
	*/
	@Override
	public final Long getIdTypeProduit() {	
		return this.idTypeProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final void setIdTypeProduit(final Long pIdTypeProduit) {	
		this.idTypeProduit = pIdTypeProduit;	
	}


		
	/**
	* {@inheritDoc}
	*/
	@Override
	public final String getTypeProduit() {	
		return this.typeProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final void setTypeProduit(final String pTypeProduit) {	
		this.typeProduit = pTypeProduit;	
	}


		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<? extends SousTypeProduitI> getSousTypeProduits() {

		/*
		 * Retourne une COPIE immuable (snapshot) de la liste des sous-types
		 * pour éviter toute modification externe ET toute incohérence
		 * en cas de modifications concurrentes internes.
		 */
		synchronized (this) {
			return Collections.unmodifiableList(
					new ArrayList<>(this.sousTypeProduits));
		}
	}
	
	
		  
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setSousTypeProduits(
	        final List<? extends SousTypeProduitI> pSousTypeProduits) {

	    /*
	     * Traite le cas d'une mauvaise instance dans pSousTypeProduits.
	     */
	    this.traiterMauvaiseInstanceDansListe(pSousTypeProduits);

	    synchronized (this) {

	        /*
	         * 1) Détache tous les enfants actuels
	         */
	        final List<SousTypeProduitI> snapshot =
	                new ArrayList<>(this.sousTypeProduits);

	        for (final SousTypeProduitI stp : snapshot) {
	            if (stp != null) {
	                stp.setTypeProduit(null);
	            }
	        }

	        /*
	         * 2) Si nouvelle liste null → terminé
	         */
	        if (pSousTypeProduits == null) {
	            return;
	        }

	        /*
	         * 3) Rattache via le setter canonique UNIQUEMENT
	         */
	        for (final SousTypeProduitI stp : pSousTypeProduits) {
	            if (stp != null) {
	                stp.setTypeProduit(this);
	            }
	        }
	    }
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de SousTypeProduitI non Objet métier 
	 * s'est glissée dans la liste pSousTypeProduits</p>
	 * <ul>
	 * <li>return si pSousTypeProduits == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si un SousTypeProduitI est une mauvaise instance 
	 * (pas TypeProduit).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduits : List&lt;? extends SousTypeProduitI&gt;
	 */
	private void traiterMauvaiseInstanceDansListe(
			final List<? extends SousTypeProduitI> pSousTypeProduits) {
		
		if (pSousTypeProduits != null) {
			for (final SousTypeProduitI stp : pSousTypeProduits) {
				
				if (stp == null) {
					continue;
				}
				
				/* LOG.fatal et throw IllegalStateException 
				 * si un SousTypeProduitI est une mauvaise instance. */
				if (!(stp instanceof SousTypeProduit)) {
					
					final String messageKo 
						= MAUVAISE_INSTANCE_METIER + stp.getClass();
					
					if (LOG.isFatalEnabled()) {
						LOG.fatal(messageKo);
					}
					
					throw new IllegalStateException(messageKo);
				}
			}
		}
		
		/* return si pSousTypeProduits == null. */
		return;		
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de SousTypeProduitI non Objet métier 
	 * est passée en paramètre d'une méthode.</p>
	 * <ul>
	 * <li>return si pSousTypeProduit == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si pSousTypeProduit est une mauvaise instance 
	 * (pas TypeProduit).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitI
	 */
	private void traiterMauvaiseInstanceSousTypeProduit(
			final SousTypeProduitI pSousTypeProduit) {
	
		if (pSousTypeProduit != null) {
	
			/*
			 * LOG.fatal et throw IllegalStateException si un
			 * pSousTypeProduit est une mauvaise instance.
			 */
			if (!(pSousTypeProduit instanceof SousTypeProduit)) {
	
				final String messageKo = MAUVAISE_INSTANCE_ENFANT_METIER
						+ pSousTypeProduit.getClass();
	
				if (LOG.isFatalEnabled()) {
					LOG.fatal(messageKo);
				}
	
				throw new IllegalStateException(messageKo);
	
			}
	
		}
	
		/* return si pTypeProduit == null. */
		return;
	}

}