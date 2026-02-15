/* ********************************************************************* */
/* ************************* OBJET METIER ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.metier.produittype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <style>p, ul, li {line-height : 1em;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">CLASSE SousTypeProduit :</p>
 * <p>modélise un <span style="font-weight:bold;">sous-type de produit</span>
 * comme : </p>
 * <ul>
 * <li>"vêtement pour homme" pour le type de produit "vêtement"</li>
 * <li>"vêtement pour femme" pour le type de produit "vêtement"</li>
 * <li>"vêtement pour enfant" pour le type de produit "vêtement"</li>
 * </ul>
 * <p>ou encore :</p>
 * <ul>
 * <li>"application mobile" pour le type de produit "logiciel"</li>
 * <li>"application web" pour le type de produit "logiciel"</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p>Dans ce modèle de PRODUIT TYPE, un TypeProduit comme "vêtement" se décline en SousTypeProduit comme :</p>
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
 * @author Daniel LEVY
 * @version 1.0
 * @created 06 décembre 2025
 */
public class SousTypeProduit  implements SousTypeProduitI, Cloneable {

	/* ------------------------- CONSTANTES ---------------------------- */	
	
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
	 * <p>"Le pTypeProduit passé en paramètre 
	 * n'est pas de type Objet Métier : "</p>
	 * </div>
	 */
	public static final String MAUVAISE_INSTANCE_PARENT_METIER 
		= "Le pTypeProduit passé en paramètre "
				+ "n'est pas de type Objet Métier : ";
	
	/**
	 * <div>
	 * <p>"Le pProduit passé en paramètre 
	 * n'est pas de type Objet Métier : "</p>
	 * </div>
	 */
	public static final String MAUVAISE_INSTANCE_PETIT_ENFANT_METIER 
		= "Le pProduit passé en paramètre "
				+ "n'est pas de type Objet Métier : ";
	
	/**
	 * <div>
	 * <p>"idSousTypeProduit;type de produit;sous-type de produit;"</p>
	 * </div>
	 */
	public static final String EN_TETE_CSV_SOUS_TYPE_PRODUIT 
		= "idSousTypeProduit;type de produit;sous-type de produit;";


	
	// ************************ATTRIBUTS**********************************/
	
	/**
	 * <div>
	 * <p>ID en base du sous-type de produit.</p>
	 * </div>
	 */
	private Long idSousTypeProduit;
	
	/**
	 * <div>
	 * <p>sous-type de produit comme :</p>
	 * <ul>
	 * <li>"vêtement pour homme"</li>
	 * <li>"vêtement pour femme"</li>
	 * </ul>
	 * <p>pour un type de produit "vêtement"</p>
	 * </div>
	 */
	private String sousTypeProduit;
	
	/**
	 * <div>
	 * <p>Type de produit auquel est rattaché le présent 
	 * sous-type de produit.</p>
	 * <p>par exemple "vêtement" pour le sous-type de produit 
	 * "vêtement pour homme".</p>
	 * <p>ATTENTION : visibilité interface.</p>
	 * </div>
	 */
	private TypeProduitI typeProduit;
	
	/**
	 * <div>
	 * <p>Collection des produits qualifiés par le présent 
	 * sous-type de produit.</p>
	 * <p>par exemple : </p>
	 * <ul>
	 * <li>"chemise à manches longues pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"chemise à manches courtes pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"sweat-shirt pour homme" pour le sous-produit "vêtement pour homme"</li>
	 * </ul>
	 * <p>ATTENTION : visibilité interface.</p>
	 * </div>
	 */
	private List<ProduitI> produits = new ArrayList<ProduitI>();
	
	
	/**
	 * <div>
	 * <p>boolean qui indique si le présent SousTypeProduit 
	 * possède un TypeProduit non null.</p>
	 * <ul>
	 * <li>true si le présent SousTypeProduit possède un TypeProduit non null.</li>
	 * </ul>
	 * <p>Doit être calculé et jamais serializé.</p>
	 * </div>
	 */
	private transient boolean valide;

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
		= LogManager.getLogger(SousTypeProduit.class);
	
/* ===============================METHODES ============================ */
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public SousTypeProduit() {
		this(null, null, null, null);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : String : sous-type de produit.
	 */
	public SousTypeProduit(final String pSousTypeProduit) {
		this(null, pSousTypeProduit, null, null);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 */
	public SousTypeProduit(final String pSousTypeProduit
			, final TypeProduitI pTypeProduit) {
		this(null, pSousTypeProduit, pTypeProduit, null);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 3 utile.</p>
	 * </div>
	 *
	 * @param pIdSousTypeProduit : Long : ID en base du sous-type de produit.
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 */
	public SousTypeProduit(final Long pIdSousTypeProduit
			, final String pSousTypeProduit
			, final TypeProduitI pTypeProduit) {
		this(pIdSousTypeProduit, pSousTypeProduit, pTypeProduit, null);
	}
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 3.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 * @param pProduits : List&lt;ProduitI&gt; : Collection des produits 
	 * qualifiés par le présent sous-type de produit.
	 */
	public SousTypeProduit(
			final String pSousTypeProduit
				, final TypeProduitI pTypeProduit
					, final List<ProduitI> pProduits) {
		this(null, pSousTypeProduit, pTypeProduit, pProduits);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR COMPLET.</p>
	 * <ul>
	 * <li>passe les paramètres aux propriétés.</li>
	 * <li>passe pTypeProduit à <code>this.typeProduit</code> en utilisant 
	 * le Setter canonique qui maintient la cohérence des données.</li>
	 * <li>alimente la liste sousTypeProduits 
	 * dans pTypeProduit avec this.</li>
	 * <li>passe <code>this.valide</code> à true 
	 * si pTypeProduit n'est pas null.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pIdSousTypeProduit : Long : ID en base du sous-type de produit.
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 * @param pProduits : List&lt;ProduitI&gt; : Collection des produits 
	 * qualifiés par le présent sous-type de produit.
	 */
	public SousTypeProduit(final Long pIdSousTypeProduit
			, final String pSousTypeProduit
				, final TypeProduitI pTypeProduit
					, final List<ProduitI> pProduits) {
		
		super();
		
		this.idSousTypeProduit = pIdSousTypeProduit;
		this.sousTypeProduit = pSousTypeProduit;
		
		/* Canonique : toujours initialiser les listes */
        this.produits = new ArrayList<>();

        /* passe pTypeProduit à this.typeProduit en utilisant le 
         * Setter canonique qui maintient la cohérence des données. */
        /* Canonique : on passe par le setter d’association 
         * (maintient les 2 côtés). */
        this.setTypeProduit(pTypeProduit);

        /* on remplit la liste this.produits après avoir 
         * passé le TypeProduit via le setter d'association. */
        if (pProduits != null) {
            for (final ProduitI p : pProduits) {
                this.ajouterSTPauProduit(p);
            }
        }

        /* passe this.valide à true si this.typeProduit 
         * n'est pas null. */
        this.recalculerValide();
		
	}

	

	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">hashCode() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">typeProduit</li>
	* <li style="font-weight:bold;">sousTypeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final int hashCode() {
		
	    final TypeProduitI typeSnapshot;
	    final String sousSnapshot;
	    
	    synchronized (this) {
	        typeSnapshot = this.typeProduit;
	        sousSnapshot = this.sousTypeProduit;
	    }

	    final int typeHash 
	    	= (typeSnapshot == null) ? 0 : typeSnapshot.hashCode();
	    final int sousHash 
	    	= (sousSnapshot == null) 
	    		? 0 : sousSnapshot.toLowerCase(Locale.ROOT).hashCode();

	    return 31 * typeHash + sousHash;
	}



	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">equals() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">typeProduit</li>
	* <li style="font-weight:bold;">sousTypeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final boolean equals(final Object pObject) {
	    /* retourne true si les références sont identiques. */
	    if (this == pObject) {
	        return true;
	    }

	    /* return false si pObject == null. */
	    if (pObject == null) {
	        return false;
	    }

	    /* retourne false si pObject n'est pas une bonne instance. */
	    if (!(pObject instanceof SousTypeProduit other)) {
	        return false;
	    }

	    /* equals sur [SousTypeProduit]. */
	    final int thisHash = System.identityHashCode(this);
	    final int otherHash = System.identityHashCode(other);

	    if (thisHash < otherHash) {
	        synchronized (this) {
	            synchronized (other) {
	                final TypeProduitI typeA = this.typeProduit;
	                final TypeProduitI typeB = other.typeProduit;
	                if (!Objects.equals(typeA, typeB)) {
	                    return false;
	                }

	                final String a = this.sousTypeProduit;
	                final String b = other.sousTypeProduit;
	                if (a == null) {
	                    return b == null;
	                }
	                if (b == null) {
	                    return false;
	                }
	                return a.equalsIgnoreCase(b);
	            }
	        }
	    } else if (thisHash > otherHash) {
	        synchronized (other) {
	            synchronized (this) {
	                final TypeProduitI typeA = this.typeProduit;
	                final TypeProduitI typeB = other.typeProduit;
	                if (!Objects.equals(typeA, typeB)) {
	                    return false;
	                }

	                final String a = this.sousTypeProduit;
	                final String b = other.sousTypeProduit;
	                if (a == null) {
	                    return b == null;
	                }
	                if (b == null) {
	                    return false;
	                }
	                return a.equalsIgnoreCase(b);
	            }
	        }
	    }

	    /* Cas rarissime : collision de System.identityHashCode(...)
	     * -> verrou de départ unique pour imposer un ordre stable
	     * et éviter tout deadlock.
	     */
	    synchronized (SousTypeProduit.class) {
	        synchronized (this) {
	            synchronized (other) {
	                final TypeProduitI typeA = this.typeProduit;
	                final TypeProduitI typeB = other.typeProduit;
	                if (!Objects.equals(typeA, typeB)) {
	                    return false;
	                }

	                final String a = this.sousTypeProduit;
	                final String b = other.sousTypeProduit;
	                if (a == null) {
	                    return b == null;
	                }
	                if (b == null) {
	                    return false;
	                }
	                return a.equalsIgnoreCase(b);
	            }
	        }
	    }
	}



	/** * {@inheritDoc} */
	@Override
	public final String toString() {

	    /* * Génère une représentation textuelle thread-safe. */
	    final Long idSnapshot;
	    final String sousTypeSnapshot;
	    final TypeProduitI typeSnapshot;

	    synchronized (this) {
	        idSnapshot = this.idSousTypeProduit;
	        sousTypeSnapshot = this.sousTypeProduit;
	        typeSnapshot = this.typeProduit;
	    }

	    final StringBuilder builder = new StringBuilder();
	    
	    builder.append("SousTypeProduit [");
	    builder.append("idSousTypeProduit=");
	    if (idSnapshot != null) {
	        builder.append(idSnapshot);
	    } else {
	        builder.append(NULL);
	    }
	    builder.append(VIRGULE_ESPACE);
	    
	    builder.append("sousTypeProduit=");
	    if (sousTypeSnapshot != null) {
	        builder.append(sousTypeSnapshot);
	    } else {
	        builder.append(NULL);
	    }
	    builder.append(VIRGULE_ESPACE);
	    
	    builder.append("typeProduit=");
	    if (typeSnapshot != null) {
	        builder.append(typeSnapshot);
	    } else {
	        builder.append(NULL);
	    }
	    builder.append(CROCHET_FERMANT);

	    return builder.toString();

	}

	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final String afficherSousTypeProduit() {
		
		if (this.getSousTypeProduit() == null) {
			return NULL;
		}
		
		return this.getSousTypeProduit();
	}



	/**
	* {@inheritDoc}
	* <div>
	* <ol>
	* <p style="font-weight:bold;">Classe dans l'ordre alphabétique de :</p>
	* <li style="font-weight:bold;">typeProduit</li>
	* <li style="font-weight:bold;">sousTypeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final int compareTo(final SousTypeProduitI pObject) {

	    /* Comparaison de la même instance retourne toujours 0. */
	    if (this == pObject) {
	        return 0;
	    }

	    /* Comparaison avec null retourne toujours < 0. */
	    if (pObject == null) {
	        return -1;
	    }

	    /* Comparaison sur [TypeProduit - SousTypeProduit]. */
	    return this.compareFields(pObject);

	}
	    
	
	
	/**
	 * Compare les champs de manière thread-safe en accédant
	 * directement aux champs et pas aux getters
	 * (pas toujours Thread-Safe).
	 * @param pObject : SousTypeProduitI :
	 * L'objet à comparer avec this.
	 * @return Le résultat de la comparaison.
	 */
	private int compareFields(final SousTypeProduitI pObject) {

	    /* L'implémentation de SousTypeProduitI est SousTypeProduit. */
	    final SousTypeProduit other = (SousTypeProduit) pObject;

	    /* Snapshots des champs nécessaires pour comparer hors verrous. */
	    final TypeProduitI typeA;
	    final String sousA;
	    final TypeProduitI typeB;
	    final String sousB;

	    /* Détermine l'ordre de verrouillage pour éviter les deadlocks.
	     * L'ordre est basé sur System.identityHashCode() pour garantir
	     * un verrouillage systématique et reproductible.
	     */
	    final int thisHash = System.identityHashCode(this);
	    final int otherHash = System.identityHashCode(other);

	    if (thisHash < otherHash) {

	        /* Verrouillage ordonné : this puis other. */
	        synchronized (this) {
	            synchronized (other) {
	                typeA = this.typeProduit;
	                sousA = this.sousTypeProduit;
	                typeB = other.typeProduit;
	                sousB = other.sousTypeProduit;
	            }
	        }

	    } else if (thisHash > otherHash) {

	        /* Verrouillage ordonné : other puis this. */
	        synchronized (other) {
	            synchronized (this) {
	                typeA = this.typeProduit;
	                sousA = this.sousTypeProduit;
	                typeB = other.typeProduit;
	                sousB = other.sousTypeProduit;
	            }
	        }

	    } else {

	        /* Cas rarissime : collision de System.identityHashCode(...)
	         * -> verrou de départ unique pour imposer un ordre stable
	         * et éviter tout deadlock.
	         */
	        synchronized (SousTypeProduit.class) {
	            synchronized (this) {
	                synchronized (other) {
	                    typeA = this.typeProduit;
	                    sousA = this.sousTypeProduit;
	                    typeB = other.typeProduit;
	                    sousB = other.sousTypeProduit;
	                }
	            }
	        }

	    }

	    /* Comparaison hors verrous pour réduire la contention. */
	    if (typeA == null) {
	        return (typeB == null) ? 0 : +1;
	    }

	    if (typeB == null) {
	        return -1;
	    }

	    final int compareTP = typeA.compareTo(typeB);
	    if (compareTP != 0) {
	        return compareTP;
	    }

	    if (sousA == null) {
	        return (sousB == null) ? 0 : +1;
	    }

	    if (sousB == null) {
	        return -1;
	    }

	    /* Comparaison case-insensitive des chaînes de caractères. */
	    return Strings.CI.compare(sousA, sousB);

	}

	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final SousTypeProduit clone() throws CloneNotSupportedException {		
		return this.cloneDeep();
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Instancie un {@link CloneContext} et appelle 
	 * {@code deepClone(ctxt)} en lui passant le CloneContext.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Appeler {@code deepClone(ctxt)} 
	 * en lui passant un nouveau {@link CloneContext}.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>méthode appelée par {@code clone()}.</li>
	 * <li>méthode private interne invisible.</li>
	 * </ul>
	 * </div>
	 *
	 * @return SousTypeProduit : clone profond.
	 */
	private SousTypeProduit cloneDeep() {	
		return deepClone(new CloneContext());	
	}
	
	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SousTypeProduit deepClone(final CloneContext ctx) {

	    final SousTypeProduit clone;
	    final TypeProduitI typeProduitProv;
	    final List<ProduitI> produitsSafeCopy;

	    synchronized (this) {

	        /* Sécurise le couple get/put dans le même verrou.
	         * Objectif : garantir l'unicité du clone 
	         * même si le même CloneContext
	         * est partagé entre threads.
	         */
	        synchronized (ctx) {

	        	/* Vérifie que le clone n'existe pas déjà dans le contexte.
	    	     * Le cas échéant, retourne le clone déjà existant.
	    	     */
	            final SousTypeProduit existing = ctx.get(this);
	            if (existing != null) {
	                return existing;
	            }

	            /* Crée un clone sans parent ni enfants 
	             * de manière thread-safe. */
	            clone = new SousTypeProduit(
	                    this.idSousTypeProduit,
	                    this.sousTypeProduit,
	                    null,
	                    null
	            );

	            /* Met le clone sans parent ni enfants dans le contexte. */
	            ctx.put(this, clone);

	        }

	        /* Snapshots thread-safe des dépendances 
	         * à cloner hors verrou. */
	        typeProduitProv = this.typeProduit;

	        /* Copie thread-safe de la liste des enfants.
	         * On évite tout Raw Type : 
	         * on reconstruit une liste typée ProduitI.
	         */
	        produitsSafeCopy = new ArrayList<ProduitI>();
	        
	        for (final Object objet : this.produits) {
	            if (objet instanceof final ProduitI produit) {
	                produitsSafeCopy.add(produit);
	            }
	        }

	    }

	    /* Clone le parent TypeProduit (si présent) 
	     * et recolle le clone parent
	     * au clone via le setter canonique.
	     */
	    if (typeProduitProv != null) {
	        final TypeProduitI cloneTypeProduit 
	        	= typeProduitProv.deepClone(ctx);
	        clone.setTypeProduit(cloneTypeProduit);
	    }

	    /* Clone les enfants Produit hors verrous 
	     * pour réduire la contention. */
	    for (final ProduitI produit : produitsSafeCopy) {
	        if (produit != null) {
	            final ProduitI cloneProduit = produit.deepClone(ctx);
	            cloneProduit.setSousTypeProduit(clone);
	        }
	    }

	    return clone;

	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SousTypeProduit cloneWithoutParentAndChildren() {

	    /* Snapshots thread-safe des propriétés scalaires de l'original. */
	    final Long idSnapshot;
	    final String sousTypeSnapshot;

	    synchronized (this) {
	        idSnapshot = this.idSousTypeProduit;
	        sousTypeSnapshot = this.sousTypeProduit;
	    }

	    /* Crée un clone sans parent ni enfants. */
	    final SousTypeProduit clone = new SousTypeProduit();
	    clone.idSousTypeProduit = idSnapshot;
	    clone.sousTypeProduit = sousTypeSnapshot;
	    clone.typeProduit = null;

	    /* Canonique : toujours initialiser les listes. */
	    clone.produits = new ArrayList<ProduitI>();

	    /* Recalcule l'état de validité du clone. */
	    clone.recalculerValide();

	    return clone;

	}



	/**
	 * <div>
	 * <p>passe <code>this.valide</code> à true 
	 * si <code>this.typeProduit</code> n'est pas null.</p>
	 *</div>
	 */
	private void recalculerValide() {
        this.valide = (this.typeProduit != null);
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
	 * <p style="font-weight:bold;">
	 * retourne une String pour afficher l'en-tête
	 * d'un SousTypeProduit en csv.</p>
	 * </div>
	 *
	 * @return String :
	 * "idSousTypeProduit;type de produit;sous-type de produit;"
	 */
	@Override
	public final String getEnTeteCsv() {
	    return EN_TETE_CSV_SOUS_TYPE_PRODUIT;
	}


	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">retourne une String pour
	 * l'affichage d'un SOUS-TYPE-PRODUIT
	 * sous forme de csv avec des séparateurs point-virgule ';'.</p>
	 * <p>Le csv retourné respecte l'ordre indiqué dans l'en-tête :
	 * "idSousTypeProduit;type de produit;sous-type de produit;"</p>
	 * <ul>
	 * <p>par exemple : </p>
	 * <li>idSousTypeProduit;type de produit;sous-type de produit;</li>
	 * <li>null;vêtement;vêtement pour homme;</li>
	 * </ul>
	 * </div>
	 *
	 * @return String :
	 * "idSousTypeProduit;type de produit;sous-type de produit;"
	 */
	@Override
	public final String toStringCsv() {
		
	    final StringBuilder builder = new StringBuilder();

	    synchronized (this) {
	        /* idSousTypeProduit */
	        builder.append(this.idSousTypeProduit != null
	            ? this.idSousTypeProduit : NULL);
	        builder.append(POINT_VIRGULE);

	        /* type de produit */
	        builder.append(this.typeProduit != null 
	        		&& this.typeProduit.getTypeProduit() != null
	            ? this.typeProduit.getTypeProduit() : NULL);
	        builder.append(POINT_VIRGULE);

	        /* sous-type de produit */
	        builder.append(this.sousTypeProduit != null
	            ? this.sousTypeProduit : NULL);
	        builder.append(POINT_VIRGULE);
	    }

	    return builder.toString();
	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">en-tête Jtable 
	 * pour un SousTypeProduit</b> :</p>
	 * <p>"idSousTypeProduit;type de produit;sous-type de produit;".</p>
	 * </div>
	 */
	@Override
	public final String getEnTeteColonne(final int pI) {

	    String entete = null;

	    switch (pI) {

	        case 0:
	            entete = "idSousTypeProduit";
	            break;

	        case 1:
	            entete = "type de produit";
	            break;

	        case 2:
	            entete = "sous-type de produit";
	            break;

	        default:
	            entete = "invalide";
	            break;

	    } // Fin du Switch._________________________________

	    return entete;

	} // Fin de getEnTeteColonne(...)._____________________________________



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">ligne Jtable 
	 * pour un SousTypeProduit</b> :</p>
	 * <p>"idSousTypeProduit;type de produit;sous-type de produit;".</p>
	 * </div>
	 */
	@Override
	public final Object getValeurColonne(final int pI) {

	    final Long idSnapshot;
	    final String sousTypeSnapshot;
	    final TypeProduitI typeProduitSnapshot;

	    /* Snapshots thread-safe pour construire le résultat hors verrou
	     * et réduire la contention.
	     */
	    synchronized (this) {
	        idSnapshot = this.idSousTypeProduit;
	        sousTypeSnapshot = this.sousTypeProduit;
	        typeProduitSnapshot = this.typeProduit;
	    }

	    Object valeur = null;

	    switch (pI) {

	        case 0:
	            if (idSnapshot != null) {
	                valeur = String.valueOf(idSnapshot);
	            }
	            break;

	        case 1:
	            if (typeProduitSnapshot != null) {
	                /* Délègue au parent via une méthode snapshotée
	                 * pour éviter une lecture non déterministe du parent.
	                 */
	                valeur = typeProduitSnapshot.getValeurColonne(1);
	            }
	            break;

	        case 2:
	            if (sousTypeSnapshot != null) {
	                valeur = sousTypeSnapshot;
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
	public final void ajouterSTPauProduit(final ProduitI pProduit) {

	    /* traite le cas d'une mauvaise instance passée en paramètre. */
	    this.traiterMauvaiseInstanceProduit(pProduit);

	    if (pProduit == null) {
	        return;
	    }

	    /* IMPORTANT : cette méthode n'est volontairement pas synchronisée
	     * pour éviter les deadlocks STP <-> Produit.
	     *
	     * Canonique : on passe uniquement par le Setter 
	     * d'association de l'enfant,
	     * qui assure la cohérence bidirectionnelle 
	     * et met à jour la liste produits
	     * via internalAddProduit(...) côté parent.
	     */
	    pProduit.setSousTypeProduit(this);

	} // Fin de ajouterSTPauProduit(...).__________________________________



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void retirerSTPauProduit(final ProduitI pProduit) {

	    /* traite le cas d'une mauvaise instance passée en paramètre. */
	    this.traiterMauvaiseInstanceProduit(pProduit);

	    if (pProduit == null) {
	        return;
	    }

	    /* IMPORTANT : cette méthode n'est volontairement pas synchronisée
	     * pendant l'appel au Setter de l'enfant pour éviter les deadlocks.
	     *
	     * On vérifie sous verrou local uniquement si pProduit est bien
	     * contenu dans la liste, puis on relâche le verrou avant de
	     * déclencher la bidirectionnalité 
	     * via le Setter canonique de l'enfant.
	     */
	    final boolean contient;

	    synchronized (this) {
	        contient = this.produits.contains(pProduit);
	    }

	    if (!contient) {
	        return;
	    }

	    /* Canonique : on passe uniquement 
	     * par le Setter d'association de l'enfant,
	     * qui assure la cohérence bidirectionnelle 
	     * et met à jour la liste produits
	     * via internalRemoveProduit(...) côté parent.
	     */
	    pProduit.setSousTypeProduit(null);

	} // Fin de retirerSTPauProduit(...).__________________________________



	/**
	 * <div>
	 * <p>ajoute pProduit à la <code>List&lt;ProduitI&gt;</code>
	 * <code style="font-weight:bold;">this.produits</code> si
	 * la liste ne le contient pas déjà.</p>
	 * <p>traite le cas d'une mauvaise instance passée en paramètre.</p>
	 * <p>Méthode interne au Package (protected)
	 * utilisée par l'enfant Produit pour éviter les boucles.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	protected final void internalAddProduit(final ProduitI pProduit) {
		
	    /* traite le cas d'une mauvaise instance passée en paramètre. */
	    this.traiterMauvaiseInstanceProduit(pProduit);

	    if (pProduit == null) {
	        return;
	    }

	    /*
	     * Ajout thread-safe à la liste.
	     */
	    synchronized (this) {
	        if (!this.produits.contains(pProduit)) {
	            this.produits.add(pProduit);
	        }
	    }
	}



	/**
	 * <div>
	 * <p>retire pProduit de la <code>List&lt;ProduitI&gt;</code>
	 * <code style="font-weight:bold;">this.produits</code></p>
	 * <p>traite le cas d'une mauvaise instance passée en paramètre.</p>
	 * <p>Méthode interne au Package (protected)
	 * utilisée par l'enfant Produit pour éviter les boucles.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	protected final void internalRemoveProduit(final ProduitI pProduit) {
	    /* traite le cas d'une mauvaise instance passée en paramètre. */
	    this.traiterMauvaiseInstanceProduit(pProduit);

	    if (pProduit == null) {
	        return;
	    }

	    /*
	     * Retrait thread-safe de la liste.
	     */
	    synchronized (this) {
	        this.produits.remove(pProduit);
	    }
	}

	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final boolean isValide() {
		return this.valide;
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Long getIdSousTypeProduit() {
	    /*
	     * Retourne l'ID de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.idSousTypeProduit;
	    }
	}


		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setIdSousTypeProduit(final Long pIdSousTypeProduit) {
	    /*
	     * Met à jour l'ID de manière thread-safe.
	     */
	    synchronized (this) {
	        this.idSousTypeProduit = pIdSousTypeProduit;
	    }
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getSousTypeProduit() {
	    /*
	     * Retourne la valeur de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.sousTypeProduit;
	    }
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setSousTypeProduit(final String pSousTypeProduit) {
	    /*
	     * Met à jour la valeur de manière thread-safe.
	     * Aucun traitement de normalisation (trim/null) n'est appliqué
	     * dans les objets métier.
	     */
	    synchronized (this) {
	        this.sousTypeProduit = pSousTypeProduit;
	    }
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeProduitI getTypeProduit() {
	    /*
	     * Retourne le parent de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.typeProduit;
	    }
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setTypeProduit(final TypeProduitI pTypeProduit) {
		
	    /* traite le cas d'une mauvaise instance. */
	    this.traiterMauvaiseInstanceTypeProduit(pTypeProduit);

	    /*
	     * Stocke l'ancien TypeProduit pour le détacher si nécessaire.
	     * IMPORTANT : cette méthode n'est volontairement pas synchronisée
	     * pour éviter les deadlocks. La synchronisation doit être réalisée
	     * par la méthode canonique Thread-Safe côté parent :
	     * TypeProduit.rattacherEnfantSTP(...) / TypeProduit.detacherEnfantSTP(...).
	     */
        final TypeProduitI old = this.typeProduit;

        /* ne fait rien et return si 
         * pTypeProduit == this.typeProduit. */
        if (old == pTypeProduit) {
            return;
        }

        /* détache le présent SousTypeProduit de l’ancien parent. */
        if (old instanceof TypeProduit oldImplTP) {
            oldImplTP.internalRemoveSousTypeProduit(this);
        }

        /* passe la nouvelle valeur pTypeProduit à this.typeProduit. */
        this.typeProduit = pTypeProduit;

        /* rattache le présent SousTypeProduit au nouveau parent
         * et l'ajoute dans la liste sousTypeProduits du parent. */
        if (pTypeProduit instanceof TypeProduit newImplTP) {
            newImplTP.internalAddSousTypeProduit(this);
        }

        /* recalcule this.valide. */
        this.recalculerValide();
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<? extends ProduitI> getProduits() {
	    /*
	     * Retourne une copie immuable de la liste des produits
	     * pour éviter les modifications externes.
	     * Le bloc synchronized garantit que l'accès à la liste
	     * est sécurisé contre les modifications concurrentes.
	     */
	    synchronized (this) {
	        return Collections.unmodifiableList(this.produits);
	    }
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setProduits(
			final List<? extends ProduitI> pProduits) {
		
	    /* traite le cas d'une mauvaise instance dans pProduits. */
	    this.traiterMauvaiseInstanceDansListeProduits(pProduits);

	    /*
	     * Détache tous les ProduitI enfants 
	     * de la présente liste this.produits
	     * en utilisant le Setter canonique de l'enfant ProduitI.
	     * Utilisation d'une copie pour éviter 
	     * ConcurrentModificationException.
	     */
	    final List<ProduitI> safeCopyDetach 
	    	= new ArrayList<>(this.produits);
	    
	    for (final ProduitI produit : safeCopyDetach) {
	        if (produit != null) {
	            produit.setSousTypeProduit(null);
	        }
	    }

	    /*
	     * Vide la liste avec clear() si pProduits == null.
	     * Ne jamais créer une nouvelle liste avec new ArrayList()
	     * pour être Hibernate-safe.
	     */
	    if (pProduits == null) {
	    	
	        synchronized (this) {
	            this.produits.clear();
	            
	        }
	    } else {
	        /*
	         * Attache les nouveaux ProduitI enfants contenus dans pProduits
	         * en utilisant le Setter canonique de l'enfant ProduitI.
	         * Utilisation d'une copie immuable pour éviter
	         * ConcurrentModificationException.
	         */
	        final List<? extends ProduitI> safeCopyAttach =
	            Collections.unmodifiableList(new ArrayList<>(pProduits));
	        
	        for (final ProduitI produit : safeCopyAttach) {
	            this.ajouterSTPauProduit(produit);
	        }
	    }
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de TypeProduitI non Objet métier 
	 * est passée en paramètre d'une méthode.</p>
	 * <ul>
	 * <li>return si pTypeProduit == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si pTypeProduit est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduitI
	 */
	private void traiterMauvaiseInstanceTypeProduit(
			final TypeProduitI pTypeProduit) {
	
		if (pTypeProduit != null) {
	
			/*
			 * LOG.fatal et throw IllegalStateException si un
			 * pTypeProduit est une mauvaise instance.
			 */
			if (!(pTypeProduit instanceof TypeProduit)) {
	
				final String messageKo = MAUVAISE_INSTANCE_PARENT_METIER
						+ pTypeProduit.getClass();
	
				if (LOG.isFatalEnabled()) {
					LOG.fatal(messageKo);
				}
	
				throw new IllegalStateException(messageKo);
	
			}
	
		}
	
		/* return si pTypeProduit == null. */
		return;
	}	



	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de ProduitI non Objet métier 
	 * est passée en paramètre d'une méthode.</p>
	 * <ul>
	 * <li>return si pProduit == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si pProduit est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	private void traiterMauvaiseInstanceProduit(
			final ProduitI pProduit) {
	
		if (pProduit != null) {
	
			/*
			 * LOG.fatal et throw IllegalStateException si un
			 * pProduit est une mauvaise instance.
			 */
			if (!(pProduit instanceof Produit)) {
	
				final String messageKo 
					= MAUVAISE_INSTANCE_PETIT_ENFANT_METIER
						+ pProduit.getClass();
	
				if (LOG.isFatalEnabled()) {
					LOG.fatal(messageKo);
				}
	
				throw new IllegalStateException(messageKo);
	
			}
	
		}
	
		/* return si pProduit == null. */
		return;
	}	
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une
	 * mauvaise instance de ProduitI non Objet métier
	 * s'est glissée dans la liste pProduits</p>
	 * <ul>
	 * <li>return si pProduits == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException
	 * si un ProduitI est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pProduits : List&lt;? extends ProduitI&gt;
	 */
	private void traiterMauvaiseInstanceDansListeProduits(
	        final List<? extends ProduitI> pProduits) {
		
	    if (pProduits != null) {
	        /*
	         * Utilisation d'une copie pour éviter 
	         * les modifications concurrentes
	         * pendant l'itération.
	         */
	        final List<? extends ProduitI> safeCopy 
	        	= new ArrayList<>(pProduits);
	        
	        for (final ProduitI p : safeCopy) {
	        	
	            if (p == null) {
	                continue;
	            }
	            
	            /*
	             * LOG.fatal et throw IllegalStateException
	             * si un ProduitI est une mauvaise instance.
	             */
	            if (!(p instanceof Produit)) {
	                final String messageKo =
	                    MAUVAISE_INSTANCE_PETIT_ENFANT_METIER + p.getClass();
	                if (LOG.isFatalEnabled()) {
	                    LOG.fatal(messageKo);
	                }
	                throw new IllegalStateException(messageKo);
	            }
	        }
	    }
	}

	
}