/* ********************************************************************* */
/* ************************* OBJET METIER ****************************** */
/* ********************************************************************* */
package levy.daniel.application.model.metier.produittype;

import java.util.List;

import levy.daniel.application.model.metier.IExportateurCsv;
import levy.daniel.application.model.metier.IExportateurJTable;

/**
 * <div>
 * <style>p, ul, li {line-height : 1em;}</style>
 * <p style="font-weight:bold;">INTERFACE SousTypeProduitI :
 * </p>
 * <p>Interface factorisant les comportements communs 
 * des objets métier <span style="font-weight:bold;">SousTypeProduit</span>
 * comme : </p> 
 * <ul>
 * <li>"vêtement pour homme" pour le type de produit "vêtement"</li>
 * <li>"vêtement pour femme" pour le type de produit "vêtement"</li>
 * <li>"vêtement pour enfant" pour le type de produit "vêtement"</li>
 * </ul>
 * et de leurs Entities 
 * (par exemple <span style="font-weight:bold;">
 * SousTypeProduitJPA</span>)</p>
 * </div>
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 11 janvier 2026
 */
public interface SousTypeProduitI extends Comparable<SousTypeProduitI>
								, IExportateurCsv, IExportateurJTable {
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">clone profond de manière Thread-Safe un 
	 * <code>SousTypeProduitI</code> en utilisant 
	 * un {@link CloneContext} pour garantir la cohérence des données.</p>
	 * </div>
	 * <p>Ne retourne jamais null.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Fournir un clone profond du présent SousTypeProduitI.</li>
	 * <li>Sécuriser le couple get/put dans le CloneContext 
	 * dans le même verrou pour garantir l'unicité du clone 
	 * même si le même CloneContext est partagé entre threads.</li>
	 * <li>Vérifier que le clone n'existe pas déjà dans le contexte. 
	 * Le cas échéant, retourner le clone déjà existant.</li>
	 * <li>Créer un clone "nu" (sans parent ni enfants) 
	 * de manière thread-safe.</li>
	 * <li>Cloner le parent TypeProduit (si présent) et recoller
	 * le clone parent au clone via le Setter canonique.</li>
	 * <li>Créer une copie thread-safe de la liste des éventuels enfants 
	 * pour éviter les modifications concurrentes.</li>
	 * <li>Cloner chaque enfant de manière thread-safe 
	 * via {@code enfant.deepClone(ctx)} 
	 * en le verrouillant individuellement pendant son clonage.</li>
	 * <li>Rattacher le clone profond de l'enfant au 
	 * clone parent via la méthode Thread-Safe 
	 * {@code rattacherEnfantSTP(cloneEnfant)}.</li>
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
	 * <li>Le clone ne partage aucune collection mutable 
	 * avec l’original.</li>
	 * <li>Les relations parent ↔ enfants sont 
	 * reconstruites proprement.</li>
	 * </ul>
	 * </div>
	 *
	 * @return SousTypeProduitI : clone profond.
	 * @throws CloneNotSupportedException 
	 * si le clonage n'est pas supporté.
	 */
	SousTypeProduitI clone() throws CloneNotSupportedException;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">clone profond de manière Thread-Safe un 
	 * <code>SousTypeProduitI</code> en utilisant 
	 * un {@link CloneContext} pour garantir la cohérence des données.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Fournir un clone profond du présent SousTypeProduitI.</li>
	 * <li>Sécuriser le couple get/put dans le CloneContext 
	 * dans le même verrou pour garantir l'unicité du clone 
	 * même si le même CloneContext est partagé entre threads.</li>
	 * <li>Vérifier que le clone n'existe pas déjà dans le contexte. 
	 * Le cas échéant, retourner le clone déjà existant.</li>
	 * <li>Créer un clone "nu" (sans parent ni enfants) 
	 * de manière thread-safe.</li>
	 * <li>Cloner le parent TypeProduit (si présent) et recoller
	 * le clone parent au clone via le Setter canonique.</li>
	 * <li>Créer une copie thread-safe de la liste des éventuels enfants 
	 * pour éviter les modifications concurrentes.</li>
	 * <li>Cloner chaque enfant de manière thread-safe 
	 * via {@code enfant.deepClone(ctx)} 
	 * en le verrouillant individuellement pendant son clonage.</li>
	 * <li>Rattacher le clone profond de l'enfant au 
	 * clone parent via la méthode Thread-Safe 
	 * {@code rattacherEnfantSTP(cloneEnfant)}.</li>
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
	 * <li>Le clone ne partage aucune collection mutable 
	 * avec l’original.</li>
	 * <li>Les relations parent ↔ enfants 
	 * sont reconstruites proprement.</li>
	 * </ul>
	 * </div>
	 * 
	 * @param ctx : CloneContext
	 * @return SousTypeProduitI : clone profond.
	 */
	SousTypeProduitI deepClone(CloneContext ctx);

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Construit un clone de SousTypeProduitI sans parent 
	 * <code>TypeProduitI</code> ni enfants 
	 * <code>List&lt;ProduitI&gt;</code></p>
	 * <ul>
	 * <p>ne contient que :</p>
	 * <li>this.idSousTypeProduit</li>
	 * <li>this.sousTypeProduit</li>
	 * <li>une nouvelle liste vide d'enfants 
	 * <code>List&lt;ProduitI&gt;</code></li>
	 * </ul>
	 * </div>
	 *
	 * @return SousTypeProduitI : clone sans parent ni enfants.
	 */
	SousTypeProduitI cloneWithoutParentAndChildren();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Ajoute le parent this à un enfant {@code ProduitI}
	 * </p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Traiter le cas d'une mauvaise instance passée en paramètre.</li>
	 * <li>Passer le présent SousTypeProduitI comme SousProduit parent 
	 * du produit enfant pProduit en utilisant son Setter canonique 
	 * qui maintient la cohérence des données.</li>
	 * </div>
	 *
	 * @param pProduit : ProduitI : 
	 * enfant à rattacher au parent this.
	 */
	void ajouterSTPauProduit(ProduitI pProduit);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * retire le parent this à un enfant {@code ProduitI}
	 * </p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Traiter le cas d'une mauvaise instance passée en paramètre.</li>
	 * <li>Retirer le présent SousTypeProduitI comme SousProduit parent 
	 * du produit enfant pProduit en utilisant son Setter canonique 
	 * qui maintient la cohérence des données.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pProduit : ProduitI : 
	 * enfant à détacher du parent this.
	 */
	void retirerSTPauProduit(ProduitI pProduit);

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * retourne une String affichant <code>this.sousTypeProduit</code>
	 * (libellé)
	 * ou "null" en cas de nullité.</p>
	 * <p>(exemple : "vêtement pour homme", "vêtement pour femme").</p>
	 * </div>
	 *
	 * @return String : this.sousTypeProduit
	 */
	String afficherSousTypeProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du Boolean qui indique si le présent SousTypeProduit 
	 * possède un TypeProduit non null.</p>
	 * <ul>
	 * <li>true si le présent SousTypeProduit possède 
	 * un TypeProduit non null.</li>
	 * </ul>
	 * <p>Doit être calculé et jamais serializé.</p>
	 * </div>
	 *
	 * @return boolean : true si le présent SousTypeProduit a un TypeProduit
	 */
	boolean isValide();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter de l'ID dans le stockage du sous-type de produit.</p>
	 * </div>
	 *
	 * @return <code>this.idSousTypeProduit</code> : Long
	 */
	Long getIdSousTypeProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Setter de l'ID dans le stockage du sous-type de produit.</p>
	 * </div>
	 *
	 * @param pIdSousTypeProduit : Long :
	 * valeur à passer à <code>this.idSousTypeProduit</code>.
	 */
	void setIdSousTypeProduit(Long pIdSousTypeProduit);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du Nom (libellé) du sous-type de produit comme :</p>
	 * <ul>
	 * <li>"vêtement pour homme"</li>
	 * <li>"vêtement pour femme"</li>
	 * </ul>
	 * <p>pour un type de produit "vêtement"</p>
	 * </div>
	 *
	 * @return <code>this.sousTypeProduit</code> : String
	 */
	String getSousTypeProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Setter du Nom (libellé) du sous-type de produit comme :</p>
	 * <ul>
	 * <li>"vêtement pour homme"</li>
	 * <li>"vêtement pour femme"</li>
	 * </ul>
	 * <p>pour un type de produit "vêtement"</p>
	 *
	 * @param pSousTypeProduit : String :
	 * valeur à passer à <code>this.sousTypeProduit</code>.
	 * </div>
	 */
	void setSousTypeProduit(String pSousTypeProduit);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du parent {@code TypeProduitI} auquel est rattaché le présent 
	 * sous-type de produit.</p>
	 * <p>par exemple "vêtement" pour le sous-type de produit 
	 * "vêtement pour homme".</p>
	 * </div>
	 *
	 * @return <code>this.typeProduit</code> : TypeProduitI
	 */
	TypeProduitI getTypeProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">SETTER CANONIQUE INTELLIGENT</p>
	 * <p>Setter du parent {@code TypeProduitI} auquel 
	 * est rattaché le présent SousTypeProduitI.</p>
	 * <p>par exemple "vêtement" pour le sous-type de produit 
	 * "vêtement pour homme".</p>
	 * <p>Établit ou modifie la relation bidirectionnelle
	 * entre un <code>SousTypeProduit</code> 
	 * et son <code>TypeProduit</code>.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Traiter le cas d'une mauvaise instance passée en paramètre.</li>
	 * <li>Si pTypeProduit == this.typeProduit : ne rien faire et return .</li>
	 * <li>Détacher le présent SousTypeProduit de l’ancien parent 
	 * et le <span style="font-weight:bold;">retire de sa liste</span> 
	 * sousTypeProduits.</li>
	 * <li>Passer pTypeProduit à <code>this.typeProduit</code>.</li>
	 * <li>Rattacher le présent SousTypeProduit au nouveau parent 
	 * et <span style="font-weight:bold;">l'ajouter à la liste</span> 
	 * sousTypeProduits du parent TypeProduitI.</li>
	 * <li>Passer this.valide à true si pTypeProduit n'est pas null.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>maintient la validité de this.</li>
	 * <li>Relation strictement bidirectionnelle maintenue.</li>
	 * <li>Si un ancien parent existe,
	 * le présent SousTypeProduit est retiré de sa collection enfants.</li>
	 * <li>Si un nouveau parent est fourni,
	 * le présent SousTypeProduit est ajouté à sa collection enfants.</li>
	 * <li>Aucun doublon par identité (==) n'est autorisé.</li>
	 * <li>Méthode idempotente.</li>
	 * <li>pTypeProduit peut être null (détachement).</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>Garantit la cohérence bidirectionnelle.</li>
	 * <li>méthode Thread-Safe.</li>
	 * <li>Détache proprement de l’ancien parent.</li>
	 * <li>Ordre de verrouillage déterministe pour éviter tout deadlock.</li>
	 * <li>Garantie de cohérence même en concurrence.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduitI :
	 * valeur à passer à <code>this.typeProduit</code>.
	 */
	void setTypeProduit(TypeProduitI pTypeProduit);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter de la Collection des produits qualifiés par le présent 
	 * sous-type de produit.</p>
	 * <p>par exemple : </p>
	 * <ul>
	 * <li>"chemise à manches longues pour homme" 
	 * pour le sous-produit "vêtement pour homme".</li>
	 * <li>"chemise à manches courtes pour homme" 
	 * pour le sous-produit "vêtement pour homme".</li>
	 * <li>"sweat-shirt pour homme" pour 
	 * le sous-produit "vêtement pour homme"</li>
	 * </ul>
	 * <p>ATTENTION : visibilité interface.</p>
	 * </div>
	 *
	 * @return List&lt;? extends ProduitI&gt; : 
	 * liste des produits (enfants) du présent SousTypeProduitI.
	 */
	List<? extends ProduitI> getProduits();



	/**
	 * <div>
	 * <p style="font-weight:bold;">SETTER CANONIQUE INTELLIGENT</p>
	 * <p>Setter de la Collection des produits (enfants) qualifiés 
	 * par le présent sous-type de produit.</p>
	 * <p>par exemple : </p>
	 * <ul>
	 * <li>"chemise à manches longues pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"chemise à manches courtes pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"sweat-shirt pour homme" pour le sous-produit "vêtement pour homme"</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Traiter le cas d'une mauvaise instance dans pProduits.</li>
	 * <li>Détacher (en mettant leur SousTypeProduitI à null) 
	 * tous les ProduitI enfants de la présente liste 
	 * <code style="font-weight:bold;">this.produits</code> 
	 * en utilisant le Setter canonique de l'enfant ProduitI.</li>
	 * <li>Vider la liste <code style="font-weight:bold;">
	 * this.produits</code> avec clear() si pProduits == null. 
	 * Ne jamais créer une nouvelle liste avec new ArrayList() 
	 * pour être Hibernate-safe.</li>
	 * <li>Attacher les nouveaux ProduitI enfants contenus 
	 * dans pProduits au présent parent SousTypeProduitI 
	 * en utilisant le Setter canonique de l'enfant ProduitI.</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pProduits : List&lt;? extends ProduitI&gt; :
	 * valeur à passer à <code>this.produits</code>.
	 */
	void setProduits(List<? extends ProduitI> pProduits);


}
