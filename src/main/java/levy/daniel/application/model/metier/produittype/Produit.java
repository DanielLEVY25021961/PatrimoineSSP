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
		
	    /* Clone profond avec gestion du contexte de manière thread-safe. */
	    final Produit clone;
	    final SousTypeProduitI parentProv;

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
	            final Produit existing = ctx.get(this);
	            if (existing != null) {
	                return existing;
	            }

	            /* Crée un clone sans parent de manière thread-safe. */
	            clone = new Produit();
	            clone.idProduit = this.idProduit;
	            clone.produit = this.produit;
	            clone.sousTypeProduit = null;
	            clone.recalculerValide();

	            /* Met le clone sans parent dans le contexte.
	             */
	            ctx.put(this, clone);
	        }

	        /* Snapshot thread-safe du parent à cloner hors verrou. */
	        parentProv = this.sousTypeProduit;
	    }

	    /* Clone le parent SousTypeProduit (si présent)
	     * et recolle le clone parent au clone via le Setter canonique.
	     */
	    if (parentProv != null) {
	        final SousTypeProduitI cloneParent = parentProv.deepClone(ctx);
	        clone.setSousTypeProduit(cloneParent);
	    }

	    return clone;
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
		 * OPTIMISATION :
		 * les appelants (cloneWithoutParent(), setSousTypeProduit(...))
		 * sont déjà sous synchronized(this).
		 * On évite donc le double-lock (ré-entrant mais redondant).
		 */
		this.valide = (this.sousTypeProduit != null);
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
		 * Génère une représentation CSV thread-safe
		 * en limitant au maximum la section critique.
		 */

		/* Snapshots sous verrou court. */
		final Long idProduitSnapshot;
		final String produitSnapshot;
		final SousTypeProduitI sousTypeProduitSnapshot;

		synchronized (this) {

			idProduitSnapshot = this.idProduit;
			produitSnapshot = this.produit;
			sousTypeProduitSnapshot = this.sousTypeProduit;

		}

		/* Récupération des informations "parent" hors verrou Produit. */
		String typeProduitString = NULL;
		String sousTypeProduitString = NULL;

		if (sousTypeProduitSnapshot != null) {

			final String sousTypeProduitInterne
				= sousTypeProduitSnapshot.getSousTypeProduit();

			if (sousTypeProduitInterne != null) {
				sousTypeProduitString = sousTypeProduitInterne;
			}

			final TypeProduitI typeProduitSnapshot
				= sousTypeProduitSnapshot.getTypeProduit();

			if (typeProduitSnapshot != null) {

				final String typeProduitInterne
					= typeProduitSnapshot.getTypeProduit();

				if (typeProduitInterne != null) {
					typeProduitString = typeProduitInterne;
				}

			}

		}

		/* Construction hors verrou. */
		final StringBuilder builder = new StringBuilder();

		/* idProduit */
		if (idProduitSnapshot != null) {
			builder.append(idProduitSnapshot);
		} else {
			builder.append(NULL);
		}
		builder.append(POINT_VIRGULE);

		/* type de produit */
		builder.append(typeProduitString);
		builder.append(POINT_VIRGULE);

		/* sous-type de produit */
		builder.append(sousTypeProduitString);
		builder.append(POINT_VIRGULE);

		/* produit */
		if (produitSnapshot != null) {
			builder.append(produitSnapshot);
		} else {
			builder.append(NULL);
		}
		builder.append(POINT_VIRGULE);

		return builder.toString();

	} //___________________________________________________________________



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
		 * Méthode pure : ne dépend d'aucun champ de l'instance.
		 * Pas de synchronized 
		 * (aucun accès à l'état, aucun risque de concurrence).
		 */
		switch (pI) {

			case 0:
				return "idproduit";

			case 1:
				return "type de produit";

			case 2:
				return "sous-type de produit";

			case 3:
				return "produit";

			default:
				return "invalide";

		}

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

		/* Snapshot court sous verrou, calcul hors verrou. */
		final Long idProduitSnapshot;
		final String produitSnapshot;
		final SousTypeProduitI sousTypeProduitSnapshot;

		synchronized (this) {
			idProduitSnapshot = this.idProduit;
			produitSnapshot = this.produit;
			sousTypeProduitSnapshot = this.sousTypeProduit;
		}

		switch (pI) {

			case 0:

				if (idProduitSnapshot != null) {
					return String.valueOf(idProduitSnapshot);
				}

				return null;

			case 1:

				if (sousTypeProduitSnapshot != null) {

					final TypeProduitI typeProduitSnapshot
						= sousTypeProduitSnapshot.getTypeProduit();

					if (typeProduitSnapshot != null) {

						final String typeProduitString
							= typeProduitSnapshot.getTypeProduit();

						if (typeProduitString != null) {
							return typeProduitString;
						}

					}

				}

				return null;

			case 2:

				if (sousTypeProduitSnapshot != null) {

					final String sousTypeProduitString
						= sousTypeProduitSnapshot.getSousTypeProduit();

					if (sousTypeProduitString != null) {
						return sousTypeProduitString;
					}

				}

				return null;

			case 3:

				if (produitSnapshot != null) {
					return produitSnapshot;
				}

				return null;

			default:

				return "invalide";

		}

	} // Fin de getValeurColonne(...)._____________________________________
		

	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p>Retourne le <code>TypeProduit</code> du <code>Produit</code>
	 * via son <code>SousTypeProduit</code>.</p>
	 * <ul>
	 * <li>retourne null si <code>sousTypeProduit</code> est null.</li>
	 * <li>snapshot court sous verrou, appel hors verrou.</li>
	 * </ul>
	 * </div>
	 */
	@Override
	public final TypeProduitI getTypeProduit() {

		/* Snapshot court sous verrou. */
		final SousTypeProduitI sousTypeProduitSnapshot;

		synchronized (this) {
			sousTypeProduitSnapshot = this.sousTypeProduit;
		}

		/* Appel hors verrou pour éviter tout verrouillage imbriqué. */
		if (sousTypeProduitSnapshot == null) {
			return null;
		}

		return sousTypeProduitSnapshot.getTypeProduit();

	} // Fin de getTypeProduit().___________________________________________



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p>Retourne le statut de validité du <code>Produit</code>.</p>
	 * <ul>
	 * <li>retourne true si <code>sousTypeProduit</code> 
	 * n'est pas null.</li>
	 * <li>lecture thread-safe du champ <code>valide</code>.</li>
	 * </ul>
	 * </div>
	 */
	@Override
	public final boolean isValide() {

		/* Snapshot court sous verrou pour garantir 
		 * la visibilité mémoire. */
		final boolean valideSnapshot;

		synchronized (this) {
			valideSnapshot = this.valide;
		}

		return valideSnapshot;

	} // Fin de isValide()._______________________________________________



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p>Retourne l'identifiant du <code>Produit</code>.</p>
	 * <ul>
	 * <li>lecture thread-safe du champ <code>idProduit</code>.</li>
	 * <li>snapshot court sous verrou, retour hors verrou.</li>
	 * </ul>
	 * </div>
	 */
	@Override
	public final Long getIdProduit() {

		/* Snapshot court sous verrou pour garantir la visibilité mémoire. */
		final Long idProduitSnapshot;

		synchronized (this) {
			idProduitSnapshot = this.idProduit;
		}

		return idProduitSnapshot;

	} // Fin de getIdProduit().___________________________________________


	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p>Met à jour l'identifiant du <code>Produit</code>.</p>
	 * <ul>
	 * <li>écriture thread-safe du champ <code>idProduit</code>.</li>
	 * <li>mise à jour atomique sous verrou.</li>
	 * </ul>
	 * </div>
	 */
	@Override
	public final void setIdProduit(final Long pIdProduit) {

		/* Mise à jour sous verrou pour garantir la visibilité mémoire. */
		synchronized (this) {
			this.idProduit = pIdProduit;
		}

	} // Fin de setIdProduit(...).________________________________________



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p>Retourne le libellé du <code>Produit</code>.</p>
	 * <ul>
	 * <li>lecture thread-safe du champ <code>produit</code>.</li>
	 * <li>snapshot court sous verrou, retour hors verrou.</li>
	 * </ul>
	 * </div>
	 */
	@Override
	public final String getProduit() {

		/* Snapshot court sous verrou pour garantir 
		 * la visibilité mémoire. */
		final String produitSnapshot;

		synchronized (this) {
			produitSnapshot = this.produit;
		}

		return produitSnapshot;

	} // Fin de getProduit()._____________________________________________


		
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p>Met à jour le libellé du <code>Produit</code>.</p>
	 * <ul>
	 * <li>écriture thread-safe du champ <code>produit</code>.</li>
	 * <li>aucune normalisation n'est appliquée au niveau métier.</li>
	 * </ul>
	 * </div>
	 */
	@Override
	public final void setProduit(final String pProduit) {

		/* Mise à jour sous verrou pour garantir la visibilité mémoire. */
		synchronized (this) {
			this.produit = pProduit;
		}

	} // Fin de setProduit(...).__________________________________________


	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p>Retourne le <code>SousTypeProduit</code> associé au <code>Produit</code>.</p>
	 * <ul>
	 * <li>lecture thread-safe du champ <code>sousTypeProduit</code>.</li>
	 * <li>snapshot court sous verrou, retour hors verrou.</li>
	 * </ul>
	 * </div>
	 */
	@Override
	public final SousTypeProduitI getSousTypeProduit() {

		/* Snapshot court sous verrou pour garantir la visibilité mémoire. */
		final SousTypeProduitI sousTypeProduitSnapshot;

		synchronized (this) {
			sousTypeProduitSnapshot = this.sousTypeProduit;
		}

		return sousTypeProduitSnapshot;

	} // Fin de getSousTypeProduit().______________________________________


		
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
	 * <p>Traite le cas où une mauvaise instance de 
	 * <code>SousTypeProduitI</code>
	 * non Objet métier est passée en paramètre.</p>
	 * <ul>
	 * <li>retourne sans effet si 
	 * <code>pSousTypeProduit</code> est null.</li>
	 * <li>journalise en <code>fatal</code> et lève une 
	 * <code>IllegalStateException</code>
	 * si l'instance n'est pas de type <code>SousTypeProduit</code>.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitI
	 */
	private void traiterMauvaiseInstanceSousTypeProduit(
			final SousTypeProduitI pSousTypeProduit) {

		/*
		 * Méthode pure : ne lit ni ne modifie l'état interne.
		 * Aucun synchronized nécessaire.
		 */
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

	} // Fin de traiterMauvaiseInstanceSousTypeProduit(...)._______________
		
	
}