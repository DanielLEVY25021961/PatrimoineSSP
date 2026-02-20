/* ********************************************************************* */
/* ************************* OBJET METIER ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.metier.produittype;

import levy.daniel.application.model.metier.IExportateurCsv;
import levy.daniel.application.model.metier.IExportateurJTable;

/**
 * 
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 11 janv. 2026
 */
public interface ProduitI extends Comparable<ProduitI>
								, IExportateurCsv, IExportateurJTable {


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">clone profond de manière Thread-Safe un 
	 * <code>ProduitI</code> en utilisant 
	 * un {@link CloneContext} pour garantir la cohérence des données.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Fournir un clone profond du présent ProduitI.</li>
	 * <li>Vérifier que le clone n'existe pas déjà dans le contexte. 
	 * Le cas échéant, retourner le clone déjà existant.</li>
	 * <li>Créer un clone "nu" (sans parent) de manière thread-safe.</li>
	 * <li>Cloner le parent SousTypeProduit (si présent) et 
	 * recoller le clone parent au présent clone via le 
	 * Setter canonique de la présente classe.</li>
	 * <li>Retourner le clone profond.</li>
	 * <li>Ne retourne jamais {@code null}.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si le clone est déjà stocke dans le CloneContext : 
	 * retourne le clone existant.</li>
	 * <li>délègue la création d'un {@link CloneContext} 
	 * à une méthode private interne dans chaque implémentation.</li>
	 * <li>S'appuie sur {@link #deepClone(ctxt)} 
	 * pour générer le clone profond.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>méthode Thread-Safe.</li>
	 * <li>Interdit les cycles et les duplications durant le clonage 
	 * grâce à l'utilisation d'un {@link CloneContext} encapsulant 
	 * une {@code IdentityHashMap} (cache).</li>
	 * </ul>
	 * </div>
	 *
	 * @return ProduitI : clone profond.
	 * @throws CloneNotSupportedException 
	 * si le clonage n'est pas supporté.
	 */
	ProduitI clone() throws CloneNotSupportedException;



	/**
	 * <div>
	 * <p style="font-weight:bold;">clone profond de manière Thread-Safe un 
	 * <code>ProduitI</code> en utilisant 
	 * un {@link CloneContext} pour garantir la cohérence des données.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Fournir un clone profond du présent ProduitI.</li>
	 * <li>Vérifier que le clone n'existe pas déjà dans le contexte. 
	 * Le cas échéant, retourner le clone déjà existant.</li>
	 * <li>Créer un clone "nu" (sans parent) de manière thread-safe.</li>
	 * <li>Cloner le parent SousTypeProduit (si présent) et 
	 * recoller le clone parent au présent clone via le 
	 * Setter canonique de la présente classe.</li>
	 * <li>Retourner le clone profond.</li>
	 * <li>Ne retourne jamais {@code null}.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Si le clone est déjà stocke dans le CloneContext : 
	 * retourne le clone existant.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>méthode Thread-Safe.</li>
	 * <li>Interdit les cycles et les duplications durant le clonage 
	 * grâce à l'utilisation d'un {@link CloneContext} encapsulant 
	 * une {@code IdentityHashMap} (cache).</li>
	 * </ul>
	 * </div>
	 *
	 * @param ctx : CloneContext
	 * @return ProduitI : clone profond
	 */
	ProduitI deepClone(CloneContext ctx);
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * clone un {@code ProduitI} sans "parent" {@code SousTypeProduitI}.</p>
	 * </div>
	 *
	 * @return ProduitI : 
	 * clone sans parent.
	 */
	ProduitI cloneWithoutParent();


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * retourne une String affichant <code>this.produit</code>
	 * (libellé) ou "null" en cas de nullité.</p>
	 * <p>(exemple : "chemise à manches longues pour homme", ...).</p>
	 * </div>
	 *
	 * @return String : this.produit
	 */
	String afficherProduit();


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du Type de Produit du présent PRODUIT 
	 * stocké dans <code>this.sousTypeProduit</code>.</p>
	 * <p>Doit être calculé et jamais serializé.</p>
	 * </div>
	 *
	 * @return <code>this.typeProduit</code> : TypeProduit
	 */
	TypeProduitI getTypeProduit();
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du Boolean qui indique si le présent ProduitI 
	 * possède un sousTypeProduit et un produit non null.</p>
	 * <ul>
	 * <li>true si :</li>
	 * <li style="margin-left:20px;">produit != null</li>
	 * <li style="margin-left:20px;">sousTypeProduit != null</li>
	 * <li>false sinon.</li>
	 * </ul>
	 * <p>Doit être calculé et jamais serializé.</p>
	 * </div>
	 *
	 * @return <code>this.valide</code> : boolean : 
	 * true si le present ProduitI a un sousTypeProduit 
	 * et un produit non null.
	 */
	boolean isValide();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter de l'ID dans le stockage du produit.</p>
	 * </div>
	 *
	 * @return <code>this.idProduit</code> : Long
	 */
	Long getIdProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Setter de l'ID en base du produit.</p>
	 * </div>
	 *
	 * @param pIdProduit : Long :
	 * valeur à passer à <code>this.idProduit</code>.
	 */
	void setIdProduit(Long pIdProduit);



	/**
	 * <div>
	 *<p style="font-weight:bold;">
	 *Getter du Nom (libellé) du produit comme par exemple :</p>
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
	 * @return <code>this.produit</code> : String
	 */
	String getProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Setter du Nom (libellé) du produit comme par exemple :</p>
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
	 * @param pProduit : String :
	 * valeur à passer à <code>this.produit</code>.
	 */
	void setProduit(String pProduit);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du sous-type de produit qui caractérise le présent produit.</p>
	 * <p>par exemple : "vêtement pour homme" pour un PRODUIT 
	 * "tee-shirt pour homme".</p>
	 * </div>
	 *
	 * @return <code>this.sousTypeProduit</code> : SousTypeProduitI
	 */
	SousTypeProduitI getSousTypeProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">SETTER CANONIQUE INTELLIGENT</p>
	 * <p>Setter du sous-type de produit qui caractérise 
	 * le présent produit (parent).</p>
	 * <p>par exemple : "vêtement pour homme" pour un PRODUIT 
	 * "tee-shirt pour homme".</p>
	 * <p>Établit ou modifie la relation bidirectionnelle
	 * entre un <code>Produit</code> et son parent 
	 * <code>SousTypeProduit</code>.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Traiter le cas d'une mauvaise instance de pSousTypeProduit.</li>
	 * <li>Mémoriser l'ancienne valeur du parent this.sousTypeProduit.</li>
	 * <li>Si la valeur passée en paramètre 
	 * vaut l'ancienne valeur : ne rien faire et return.</li>
	 * <li>Détacher le présent Produit de l’ancien parent SousTypeProduit 
	 * et le <span style="font-weight:bold;">
	 * retirer de sa liste produits</span>.</li>
	 * <li>Passer  <code>pSousTypeProduit</code> à 
	 * <code style="font-weight:bold;">this.sousTypeProduit</code> 
	 * (le parent SousTypeProduit du présent Produit).</li>
	 * <li>Rattacher le présent produit au nouveau parent 
	 * et <span style="font-weight:bold;">
	 * l'ajouter à sa liste produits</span>.</li>
	 * <li>Passer this.valide à true si le parent this.sousTypeProduit 
	 * n'est pas null.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Maintient la validité de this.</li>
	 * <li>Relation strictement bidirectionnelle maintenue.</li>
	 * <li>Si un ancien parent existe, le présent Produit
	 * est retiré de sa collection enfants.</li>
	 * <li>Si un nouveau parent est fourni,
	 * le présent Produit est ajouté à sa collection enfants.</li>
	 * <li>Aucun doublon par identité (==) n'est autorisé.</li>
	 * <li>Méthode idempotente :
	 * si la valeur passée est identique à l'actuelle,
	 * aucun traitement n'est effectué.</li>
	 * <li>pSousTypeProduit peut être null (détachement).</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>méthode Thread-Safe.</li>
	 * <li>Garantit la cohérence bidirectionnelle.</li>
	 * <li>Détache proprement de l’ancien parent.</li>
	 * <li>Rattache proprement au nouveau parent.</li>
	 * <li>Maintient la validité métier.</li>
	 * <li>Maintient la cohérence parent/enfant même en contexte concurrent.</li>
	 * <li>Ne provoque pas de deadlock si l’ordre de 
	 * verrouillage canonique est respecté.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitI :
	 * valeur à passer à <code>this.sousTypeProduit</code>.
	 */
	void setSousTypeProduit(SousTypeProduitI pSousTypeProduit);

	
}
