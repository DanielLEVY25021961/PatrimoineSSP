/* ********************************************************************* */
/* ************************* OBJET METIER ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.metier.produittype;

import java.util.Locale;

import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <style>p, ul, li {line-height : 1em;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE Produit :</p>
 * <p>modélise un <span style="font-weight:bold;">produit</span>
 *  comme par exemple : </p>
 * <ul>
 * <li>le produit "chemise à manches longues pour homme" 
 * pour le sous-produit "vêtement pour homme".</li>
 * <li>le produit "chemise à manches courtes pour homme" 
 * pour le sous-produit "vêtement pour homme".</li>
 * <li>le produit "sweat-shirt pour homme" 
 * pour le sous-produit "vêtement pour homme"</li>
 * </ul>
 * </div>
 * 
 * <div>
 * 
 * <div>
 * <p>Dans ce modèle de PRODUIT TYPE, un TypeProduit comme "vêtement" 
 * se décline en SousTypeProduit comme :</p>
 * <ul>
 * <li>"vêtement pour homme"</li>
 * <li>"vêtement pour femme"</li>
 * <li>"vêtement pour enfant"</li>
 * </ul>
 * </div>
 * <div>
 * <p>un SousTypeProduit comme "vêtement pour homme" 
 * qualifie des Produit comme : </p>
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
 * 
 * 
 * @author Daniel LEVY
 * @version 1.0
 * @created 06 décembre 2025 20:59:31
 */
public class Produit implements ProduitI, Cloneable {

	// ************************ATTRIBUTS************************************/
	
	/**
	 * "null"
	 */
	public static final String NULL = "null";

	
	/**
	 * ", "
	 */
	public static final String VIRGULE_ESPACE = ", ";

	
	/**
	 * ']'
	 */
	public static final char CROCHET_FERMANT = ']';

	
	/**
	 * ';'
	 */
	public static final char POINT_VIRGULE = ';';
	
	/**
	 * "le SousTypeProduit passé en paramètre 
	 * n'est pas de type Objet métier : "
	 */
	public static final String MAUVAISE_INSTANCE_ENFANT_METIER 
		= "le SousTypeProduit passé en paramètre "
				+ "n'est pas de type Objet métier : ";


	/* ----------------------------------------------------------------- */
	
	/**
	 * <div>
	 * <p>ID en base du produit.</p>
	 * </div>
	 */
	private Long idProduit;

	
	/**
	 * <div>
	 * <p>Nom du produit comme par exemple :</p>
	 * <ul>
     * <li>le produit "chemise à manches longues pour homme" 
     * pour le sous-produit "vêtement pour homme".</li>
     * <li>le produit "chemise à manches courtes pour homme" 
     * pour le sous-produit "vêtement pour homme".</li>
     * <li>le produit "sweat-shirt pour homme" 
     * pour le sous-produit "vêtement pour homme"</li>
     * </ul>
     * </div>
	 */
	private String produit;
	
	
	/**
	 * <div>
	 * <p>sous-type de produit qui caractérise le présent produit.</p>
	 * <p>par exemple : "vêtement pour homme" pour un PRODUIT 
	 * "tee-shirt pour homme".</p>
	 * <p>ATTENTION : visibilité interface.</p>
	 * </div>
	 */
	private SousTypeProduitI sousTypeProduit;
	
	
	/**
	 * <p>boolean qui indique si le présent Produit 
	 * possède un SousTypeProduit non null.</p>
	 * <ul>
	 * <li>true si le présent Produit possède un SousTypeProduit non null.</li>
	 * </ul>
	 * <p>Doit être calculé et jamais serializé.</p>
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
	private static final Logger LOG = LogManager.getLogger(Produit.class);
	
/* ===============================METHODES ============================ */

	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public Produit() {
		this(null, null, null);
	}
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * </div>
	 *
	 * @param pProduit : String : Nom du produit.
	 */
	public Produit(final String pProduit) {
		this(null, pProduit, null);
	}

	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pProduit : String : Nom du produit.
	 * @param pSousTypeProduit : SousTypeProduitI : 
	 * sous-type de produit qui caractérise le présent produit.
	 */
	public Produit(final String pProduit
				, final SousTypeProduitI pSousTypeProduit) {
		this(null, pProduit, pSousTypeProduit);
	}
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR COMPLET.</p>
	 * <ul>
	 * <li>passe <code>pIdProduit</code> 
	 * à <code style="font-weight:bold;">this.idProduit</code></li>
	 * <li>passe <code>pProduit</code> 
	 * à <code style="font-weight:bold;">this.produit</code></li>
	 * <li>appelle le Setter intelligent <code style="font-weight:bold;">
	 * this.setSousTypeProduit(pSousTypeProduit)</code> qui : </li>
	 * <ul>
	 * <li>détache le présent Produit de l’ancien parent SousTypeProduit 
	 * et le <span style="font-weight:bold;">
	 * retire de sa liste produits</span>.</li>
	 * <li>passe  <code>pSousTypeProduit</code> à 
	 * <code style="font-weight:bold;">this.sousTypeProduit</code> 
	 * (le SousTypeProduit du présent Produit).</li>
	 * <li>rattache le présent produit au nouveau parent 
	 * et <span style="font-weight:bold;">
	 * l'ajoute à sa liste produits</span>.</li>
	 * <li>passe this.valide à true si this.sousTypeProduit 
	 * n'est pas null.</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pIdProduit : Long : ID en base du produit. 
	 * @param pProduit : String : Nom du produit.
	 * @param pSousTypeProduit : SousTypeProduitI : 
	 * sous-type de produit qui caractérise le présent produit.
	 */
	public Produit(final Long pIdProduit
			, final String pProduit
				, final SousTypeProduitI pSousTypeProduit) {
		
		super();
		
		this.idProduit = pIdProduit;
        this.produit = pProduit;
        
        this.setSousTypeProduit(pSousTypeProduit);

	}

	
	
	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">hashCode() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">produit</li>
	* <li style="font-weight:bold;">sousTypeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final int hashCode() {

	    final SousTypeProduitI sousTypeProduitSnapshot;
	    final String produitSnapshot;

	    synchronized (this) {
	        sousTypeProduitSnapshot = this.sousTypeProduit;
	        produitSnapshot = this.produit;
	    }

	    final int hashProduit =
	            (produitSnapshot == null) 
	            ? 0 : produitSnapshot.toLowerCase(Locale.ROOT).hashCode();

	    int hashSousTypeProduit = 0;

	    if (sousTypeProduitSnapshot != null) {

	        final String sousTypeLibelle 
	        	= sousTypeProduitSnapshot.getSousTypeProduit();
	        final TypeProduitI typeProduitSnapshot 
	        	= sousTypeProduitSnapshot.getTypeProduit();
	        final String typeProduitLibelle =
	                (typeProduitSnapshot == null) 
	                ? null : typeProduitSnapshot.getTypeProduit();

	        final int hashSousTypeLibelle =
	                (sousTypeLibelle == null) 
	                ? 0 : sousTypeLibelle.toLowerCase(Locale.ROOT).hashCode();

	        final int hashTypeProduitLibelle =
	                (typeProduitLibelle == null) 
	                ? 0 : typeProduitLibelle.toLowerCase(Locale.ROOT).hashCode();

	        int resultStp = 1;
	        resultStp = 31 * resultStp + hashSousTypeLibelle;
	        resultStp = 31 * resultStp + hashTypeProduitLibelle;

	        hashSousTypeProduit = resultStp;

	    }

	    int result = 1;

	    result = 31 * result + hashSousTypeProduit;
	    result = 31 * result + hashProduit;

	    return result;

	}
	

	
	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">equals() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">produit</li>
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
	    if (!(pObject instanceof Produit)) {
	        return false;
	    }

	    final Produit other = (Produit) pObject;

	    final SousTypeProduitI thisSousTypeProduitSnapshot;
	    final String thisProduitSnapshot;

	    synchronized (this) {
	        thisSousTypeProduitSnapshot = this.sousTypeProduit;
	        thisProduitSnapshot = this.produit;
	    }

	    final SousTypeProduitI otherSousTypeProduitSnapshot;
	    final String otherProduitSnapshot;

	    synchronized (other) {
	        otherSousTypeProduitSnapshot = other.sousTypeProduit;
	        otherProduitSnapshot = other.produit;
	    }

	    /* produit comparé insensible à la casse. */
	    if (thisProduitSnapshot == null) {
	        if (otherProduitSnapshot != null) {
	            return false;
	        }
	    } else {
	        if (otherProduitSnapshot == null) {
	            return false;
	        }
	        if (!thisProduitSnapshot.equalsIgnoreCase(
	        		otherProduitSnapshot)) {
	            return false;
	        }
	    }

	    /* sousTypeProduit comparé métier (sans equals() transitif).
	     * - compare getSousTypeProduit() insensible à la casse.
	     * - compare le typeProduit associé via 
	     * son libellé insensible à la casse.
	     */
	    if (thisSousTypeProduitSnapshot == null) {
	        return otherSousTypeProduitSnapshot == null;
	    }

	    if (otherSousTypeProduitSnapshot == null) {
	        return false;
	    }

	    final String thisSousTypeLibelle 
	    	= thisSousTypeProduitSnapshot.getSousTypeProduit();
	    final String otherSousTypeLibelle 
	    	= otherSousTypeProduitSnapshot.getSousTypeProduit();

	    if (thisSousTypeLibelle == null) {
	        if (otherSousTypeLibelle != null) {
	            return false;
	        }
	    } else {
	        if (otherSousTypeLibelle == null) {
	            return false;
	        }
	        if (!thisSousTypeLibelle.equalsIgnoreCase(
	        		otherSousTypeLibelle)) {
	            return false;
	        }
	    }

	    final TypeProduitI thisTypeProduitSnapshot 
	    	= thisSousTypeProduitSnapshot.getTypeProduit();
	    final TypeProduitI otherTypeProduitSnapshot 
	    	= otherSousTypeProduitSnapshot.getTypeProduit();

	    if (thisTypeProduitSnapshot == null) {
	        return otherTypeProduitSnapshot == null;
	    }

	    if (otherTypeProduitSnapshot == null) {
	        return false;
	    }

	    final String thisTypeProduitLibelle 
	    	= thisTypeProduitSnapshot.getTypeProduit();
	    final String otherTypeProduitLibelle 
	    	= otherTypeProduitSnapshot.getTypeProduit();

	    if (thisTypeProduitLibelle == null) {
	        return otherTypeProduitLibelle == null;
	    }

	    if (otherTypeProduitLibelle == null) {
	        return false;
	    }

	    return thisTypeProduitLibelle
	    		.equalsIgnoreCase(otherTypeProduitLibelle);

	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {

	    /* Snapshot thread-safe des champs nécessaires. */
	    final Long idProduitSnapshot;
	    final String produitSnapshot;
	    final SousTypeProduitI sousTypeProduitSnapshot;

	    synchronized (this) {

	        /* Accès direct aux attributs pour éviter 
	         * tout appel transitif pendant le verrou. */
	        idProduitSnapshot = this.idProduit;
	        produitSnapshot = this.produit;
	        sousTypeProduitSnapshot = this.sousTypeProduit;

	    }

	    /* Construction hors verrou pour minimiser la section critique. */
	    final StringBuilder builder = new StringBuilder();

	    builder.append("Produit [");
	    builder.append("idProduit=");
	    if (idProduitSnapshot != null) {
	        builder.append(idProduitSnapshot);
	    } else {
	        builder.append(NULL);
	    }

	    builder.append(VIRGULE_ESPACE);
	    builder.append("produit=");
	    if (produitSnapshot != null) {
	        builder.append(produitSnapshot);
	    } else {
	        builder.append(NULL);
	    }

	    builder.append(VIRGULE_ESPACE);
	    builder.append("sousTypeProduit=");
	    if (sousTypeProduitSnapshot != null) {

	        /* Appel éventuel au toString() du parent hors verrou Produit. */
	        builder.append(sousTypeProduitSnapshot.toString());

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
	public String afficherProduit() {
		
		if (this.getProduit() == null) {
			return NULL;
		}
		
		return this.getProduit();
	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <ol>
	 * <p style="font-weight:bold;">Classe dans l'ordre alphabétique de :</p>
	 * <li style="font-weight:bold;">SousTypeProduit</li>
	 * <li style="font-weight:bold;">produit</li>
	 * </ol>
	 * </div>
	 */
	@Override
	public final int compareTo(final ProduitI pObject) {

	    /* retourne true si les références sont identiques. */
	    if (this == pObject) {
	        return 0;
	    }

	    /* return false si pObject == null. */
	    if (pObject == null) {
	        return -1;
	    }

	    /* Comparaison sur [SousTypeProduit - Produit]. */
	    return this.compareFields(pObject);
	}
	
	
	
	/**
	 * <div>
	 * <p>Compare les champs de manière thread-safe en accédant
	 * directement aux champs et pas aux getters
	 * (pas toujours Thread-Safe).</p>
	 *
	 * @param pObject : ProduitI :
	 * L'objet à comparer avec this.
	 * @return Le résultat de la comparaison.
	 */
	private int compareFields(final ProduitI pObject) {

	    /* L'implémentation de ProduitI est Produit. */
	    final Produit other = (Produit) pObject;

	    /* Snapshots des champs nécessaires pour comparer hors verrous. */
	    final SousTypeProduitI sousTypeProduitA;
	    final String produitA;
	    final SousTypeProduitI sousTypeProduitB;
	    final String produitB;

	    /* Détermine l'ordre de verrouillage pour éviter les deadlocks.
	     * L'ordre est basé sur System.identityHashCode() pour garantir
	     * un verrouillage systématique et reproductible. */
	    final int thisHash = System.identityHashCode(this);
	    final int otherHash = System.identityHashCode(other);

	    if (thisHash < otherHash) {

	        /* Verrouillage ordonné : this puis other. */
	        synchronized (this) {
	            synchronized (other) {
	                sousTypeProduitA = this.sousTypeProduit;
	                produitA = this.produit;
	                sousTypeProduitB = other.sousTypeProduit;
	                produitB = other.produit;
	            }
	        }

	    } else if (thisHash > otherHash) {

	        /* Verrouillage ordonné : other puis this. */
	        synchronized (other) {
	            synchronized (this) {
	                sousTypeProduitA = this.sousTypeProduit;
	                produitA = this.produit;
	                sousTypeProduitB = other.sousTypeProduit;
	                produitB = other.produit;
	            }
	        }

	    } else {

	        /* Cas rarissime : collision de System.identityHashCode(...)
	         * -> verrou de départ unique pour imposer un ordre stable
	         * et éviter tout deadlock. */
	        synchronized (Produit.class) {
	            synchronized (this) {
	                synchronized (other) {
	                    sousTypeProduitA = this.sousTypeProduit;
	                    produitA = this.produit;
	                    sousTypeProduitB = other.sousTypeProduit;
	                    produitB = other.produit;
	                }
	            }
	        }
	    }

	    /* Comparaison hors verrous pour réduire la contention. */

	    /* SousTypeProduit. */
	    if (sousTypeProduitA == null) {
	        if (sousTypeProduitB != null) {
	            return +1;
	        }
	    } else {
	        if (sousTypeProduitB == null) {
	            return -1;
	        }
	        final int compareSousTypeProduit 
	        = sousTypeProduitA.compareTo(sousTypeProduitB);
	        if (compareSousTypeProduit != 0) {
	            return compareSousTypeProduit;
	        }
	    }

	    /* Produit. */
	    if (produitA == null) {
	        return (produitB == null) ? 0 : +1;
	    }
	    if (produitB == null) {
	        return -1;
	    }

	    return Strings.CI.compare(produitA, produitB);
	}	
	

	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final Produit clone() throws CloneNotSupportedException {
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
	 * @return Produit : clone profond.
	 */
	private Produit cloneDeep() {
		return deepClone(new CloneContext());	
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Produit deepClone(final CloneContext ctx) {
		
	    /*
	     * Clone profond avec gestion du contexte de manière thread-safe.
	     */
	    synchronized (this) {
	    	
	    	/*
		     * Vérifie que le clone n'existe pas déjà dans le contexte.
		     * Le cas échéant, retourne le clone déjà existant.
		     */
	        final Produit existing = ctx.get(this);
	        
	        if (existing != null) {
	            return existing;
	        }

	        /*
		     * Crée le clone sans parent.
		     */
	        final Produit cloneP = this.cloneWithoutParent();
	        
	        /* met le clone sans parent dans le contexte. */
	        ctx.put(this, cloneP);

	        /* récupère le parent SousTypeProduit. */
	        final SousTypeProduitI stpI = this.sousTypeProduit;
	        
	        /*
		     * Clone le parent SousTypeProduit (si présent) et recolle
		     * le clone parent au présent clone via le Setter canonique.
		     */
	        if (stpI != null) {
	        	
	            final SousTypeProduitI cloneParent = stpI.deepClone(ctx);
	            cloneP.setSousTypeProduit(cloneParent);
	        }
	        
	        return cloneP;
	        
	    }
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Produit cloneWithoutParent() {
	    /*
	     * Clone sans parent de manière thread-safe.
	     */
	    synchronized (this) {
	        final Produit clone = new Produit();
	        clone.idProduit = this.idProduit;
	        clone.produit = this.produit;
	        clone.sousTypeProduit = null;
	        clone.recalculerValide();
	        return clone;
	    }
	}
	

	
	/**
	 * <div>
	 * <p>passe <code>this.valide</code> à true
	 * si <code>this.sousTypeProduit</code> n'est pas null.</p>
	 *</div>
	 */
	private void recalculerValide() {
	    /*
	     * Recalcule la validité de manière thread-safe.
	     */
	    synchronized (this) {
	        this.valide = (this.sousTypeProduit != null);
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
	    /*
	     * Méthode statique, donc thread-safe par nature.
	     */
	    if (pString == null) {
	        return null;
	    }
	    
	    final String trime = pString.trim();
	    return trime.isEmpty() ? null : trime;
	}

	
	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">retourne une String pour afficher 
	 * l'en-tête d'un Produit en csv.</p>
	 * </div>
	 *
	 * @return String : 
	 * "idproduit;type de produit;sous-type de produit;produit;"
	 */
	@Override
	public final String getEnTeteCsv() {
		return "idproduit;type de produit;sous-type de produit;produit;";
	}


	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">retourne une String pour 
	 * l'affichage d'un PRODUIT 
	 * sous forme de csv avec des séparateurs point-virgule ';'.</p>
	 * <p>Le csv retourné respecte l'ordre indiqué dans l'en-tête : 
	 * "idproduit;type de produit;sous-type de produit;produit;"</p>
	 * <ul>
	 * <p>par exemple : </p>
	 * <li>idproduit;type de produit;sous-type de produit;produit;</li>
	 * <li>null;vêtement;vêtement pour homme;chemise manches longues;</li>
	 * </ul>
	 * </div>
	 *
	 * @return String : 
	 * "this.idProduit;typeProduit;sous-type de produit;produit;"
	 */
	@Override
	public final String toStringCsv() {
	    /*
	     * Génère une représentation CSV thread-safe.
	     */
	    final StringBuilder builder = new StringBuilder();
	    synchronized (this) {
	        /* idProduit */
	        if (this.getIdProduit() != null) {
	            builder.append(this.getIdProduit());
	        } else {
	            builder.append(NULL);
	        }
	        builder.append(POINT_VIRGULE);

	        /* type de produit */
	        if (this.getTypeProduit() == null) {
	            builder.append(NULL);
	        } else if (this.getTypeProduit().getTypeProduit() == null) {
	            builder.append(NULL);
	        } else {
	            builder.append(this.getTypeProduit().getTypeProduit());
	        }
	        builder.append(POINT_VIRGULE);

	        /* sous-type de produit */
	        if (this.getSousTypeProduit() == null) {
	            builder.append(NULL);
	        } else if (
	        		this.getSousTypeProduit()
	        		.getSousTypeProduit() == null) {
	            builder.append(NULL);
	        } else {
	            builder.append(
	            		this.getSousTypeProduit().getSousTypeProduit());
	        }
	        builder.append(POINT_VIRGULE);

	        /* produit */
	        if (this.getProduit() == null) {
	            builder.append(NULL);
	        } else {
	            builder.append(this.getProduit());
	        }
	        builder.append(POINT_VIRGULE);
	    }
	    return builder.toString();
	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">en-tête Jtable pour un Produit</b> :</p>
	 * <p>"idproduit;type de produit;sous-type de produit;produit;".</p>
	 * </div>
	 */
	@Override
	public final String getEnTeteColonne(final int pI) {
	    /*
	     * Retourne l'en-tête de colonne de manière thread-safe.
	     */
	    String entete = null;
	    
	    synchronized (this) {
	    	
	        switch (pI) {
	        
	        case 0:
	            entete = "idproduit";
	            break;
	        case 1:
	            entete = "type de produit";
	            break;
	        case 2:
	            entete = "sous-type de produit";
	            break;
	        case 3:
	            entete = "produit";
	            break;
	        default:
	            entete = "invalide";
	            break;
	        }
	    }
	    
	    return entete;
	    
	} // Fin de getEnTeteColonne(...)._____________________________________



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">ligne Jtable pour un Produit</b> :</p>
	 * <p>"idproduit;type de produit;sous-type de produit;produit;".</p>
	 * </div>
	 */
	@Override
	public final Object getValeurColonne(final int pI) {
	    /*
	     * Retourne la valeur de colonne de manière thread-safe.
	     */
	    Object valeur = null;
	    synchronized (this) {
	        switch (pI) {
	        case 0:
	            if (this.getIdProduit() != null) {
	                valeur = String.valueOf(this.getIdProduit());
	            }
	            break;
	        case 1:
	            if (this.getTypeProduit() != null) {
	                if (this.getTypeProduit().getTypeProduit() != null) {
	                    valeur 
	                    	= this.getTypeProduit().getTypeProduit();
	                }
	            }
	            break;
	        case 2:
	            if (this.getSousTypeProduit() != null) {
	                if (this.getSousTypeProduit()
	                		.getSousTypeProduit() != null) {
	                    valeur 
	                    	= this.getSousTypeProduit().getSousTypeProduit();
	                }
	            }
	            break;
	        case 3:
	            if (this.getProduit() != null) {
	                valeur = this.getProduit();
	            }
	            break;
	        default:
	            valeur = "invalide";
	            break;
	        }
	    }
	    
	    return valeur;
	    
	} // Fin de getValeurColonne(...)._____________________________________

		
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeProduitI getTypeProduit() {
	    /*
	     * Retourne le TypeProduitI via le parent SousTypeProduitI 
	     * de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.sousTypeProduit != null
	                ? this.sousTypeProduit.getTypeProduit() : null;
	    }
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isValide() {
	    /*
	     * Retourne le statut de validité de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.valide;
	    }
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Long getIdProduit() {
	    /*
	     * Retourne l'ID de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.idProduit;
	    }
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setIdProduit(final Long pIdProduit) {
	    /*
	     * Met à jour l'ID de manière thread-safe.
	     */
	    synchronized (this) {
	        this.idProduit = pIdProduit;
	    }
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getProduit() {
	    /*
	     * Retourne le nom du produit de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.produit;
	    }
	}


		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setProduit(final String pProduit) {
	    /*
	     * Met à jour le nom du produit de manière thread-safe.
	     * Aucun traitement de normalisation (trim/null) n'est appliqué
	     * dans les objets métier.
	     */
	    synchronized (this) {
	        this.produit = pProduit;
	    }
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SousTypeProduitI getSousTypeProduit() {
	    /*
	     * Retourne le parent SousTypeProduitI de manière thread-safe.
	     */
	    synchronized (this) {
	        return this.sousTypeProduit;
	    }
	}


		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSousTypeProduit(
			final SousTypeProduitI pSousTypeProduit) {
	    /*
	     * Traite le cas d'une mauvaise instance de pSousTypeProduit.
	     * Synchronisation pour éviter les accès concurrents.
	     */
	    synchronized (this) {
	    	
	        /* traite le cas d'une mauvaise instance de pSousTypeProduit. */
	        this.traiterMauvaiseInstanceSousTypeProduit(pSousTypeProduit);

	        /* mémorise l'ancienne valeur de this.sousTypeProduit. */
	        final SousTypeProduitI old = this.sousTypeProduit;

	        /* ne fait rien et return si la valeur 
	         * passée en paramètre vaut l'ancienne valeur. */
	        if (old == pSousTypeProduit) {
	            return;
	        }

	        /* SYNTAXE DEPUIS Java 17 : 
	         * Si old est une instance de SousTypeProduit, 
	         * alors cast old en SousTypeProduit et le stocke 
	         * dans la variable oldImpl. */
	        if (old instanceof SousTypeProduit oldImplSTP) {
	            /* détache le présent Produit de l’ancien 
	             * parent SousTypeProduit et le retire 
	             * de sa liste produits. */
	            oldImplSTP.internalRemoveProduit(this);
	        }

	        /* passe pSousTypeProduit à this.sousTypeProduit. */
	        this.sousTypeProduit = pSousTypeProduit;

	        /* rattache le présent produit au nouveau parent 
	         * et l'ajoute à sa liste produits. */
	        if (pSousTypeProduit instanceof SousTypeProduit newImplSTP) {
	            newImplSTP.internalAddProduit(this);
	        }

	        /* passe this.valide à true si 
	         * this.sousTypeProduit n'est pas null. */
	        this.recalculerValide();
	    }
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une
	 * mauvaise instance de SousTypeProduitI non Objet métier
	 * est passée en paramètre d'une méthode.</p>
	 * <ul>
	 * <li>return si pSousTypeProduit == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException
	 * si pSousTypeProduit est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitI
	 */
	private void traiterMauvaiseInstanceSousTypeProduit(
	        final SousTypeProduitI pSousTypeProduit) {
	    /*
	     * Traite les mauvaises instances de manière thread-safe.
	     */
	    synchronized (this) {
	    	
	        if (pSousTypeProduit != null) {
	        	
	            if (!(pSousTypeProduit instanceof SousTypeProduit)) {
	            	
	                final String messageKo 
	                	= MAUVAISE_INSTANCE_ENFANT_METIER
	                        + pSousTypeProduit.getClass();
	                
	                if (LOG.isFatalEnabled()) {
	                    LOG.fatal(messageKo);
	                }
	                
	                throw new IllegalStateException(messageKo);
	            }
	        }
	    }
	}
		
	
}