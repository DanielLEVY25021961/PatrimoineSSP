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
	 * un {@link CloneContext} pour garantir la cohérence des données</p>
	 * </div>
	 *
	 * @return SousTypeProduitI : clone profond.
	 */
	SousTypeProduitI cloneDeep();
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">retourne un Clone PROFOND 
	 * du présent SousTypeProduitI</p>
	 * <ul>
	 * <li>vérifie que le clone n'existe pas déjà dans l'IdentityHashMap 
	 * (cache)
	 * du {@link CloneContext}. Le cas échéant, 
	 * retourne le clone déjà existant.</li>
	 * <li>instancie un clone "nu" sans parent ni enfants.</li>
	 * <li>Clone profond éventuellement ensuite le parent.</li>
	 * <li>Rattache le clone "nu" au clone parent 
	 * via le setter canonique de la présente.</li>
	 * <li>Clone éventuellement les enfants de la liste produits 
	 * si elle n'est pas null.</li>
	 * <li>clone profond chaque enfant de la liste.</li>
	 * <li>rattache chaque clone enfant au présent 
	 * clone via le Setter canonique 
	 * côté enfant.</li>
	 * </ul>
	 * </div>
	 *
	 * @param ctx : CloneContext.
	 * @return SousTypeProduitI : clone profond.
	 */
	SousTypeProduitI deepClone(CloneContext ctx);

	
	
	/**
	 * <div>
	 * <p>Construit un clone de SousTypeProduitI sans parent 
	 * <code>TypeProduitI</code> ni enfants 
	 * <code>List&lt;ProduitI&gt;</code></p>
	 * <ul>
	 * <p>ne contient que :</p>
	 * <li>this.idSousTypeProduit</li>
	 * <li>this.sousTypeProduit</li>
	 * <li>une nouvelle liste vide <code>List&lt;ProduitI&gt;</code></li>
	 * </ul>
	 * </div>
	 *
	 * @return SousTypeProduitI : clone sans parent ni enfants.
	 */
	SousTypeProduitI cloneWithoutParentAndChildren();



	/**
	 * <div>
	 * <p>traite le cas d'une mauvaise instance passée en paramètre.</p>
	 * <p>Passe le présent SousTypeProduitI comme SousProduit parent 
	 * du produit pProduit en utilisant son Setter canonique 
	 * qui maintient la cohérence des données.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	void ajouterSTPauProduit(ProduitI pProduit);



	/**
	 * <div>
	 * <p>traite le cas d'une mauvaise instance passée en paramètre.</p>
	 * <p>Retire le présent SousTypeProduitI comme SousProduit parent 
	 * du produit pProduit en utilisant son Setter canonique 
	 * qui maintient la cohérence des données.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	void retirerSTPauProduit(ProduitI pProduit);

	
	
	/**
	 * <div>
	 * <p>retourne une String affichant <code>this.sousTypeProduit</code>
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
	 * <p>Getter du Boolean qui indique si le présent SousTypeProduit 
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
	 * <p>Getter de l'ID en base du sous-type de produit.</p>
	 * </div>
	 *
	 * @return <code>this.idSousTypeProduit</code> : Long
	 */
	Long getIdSousTypeProduit();



	/**
	 * <div>
	 * <p>Setter de l'ID en base du sous-type de produit.</p>
	 * </div>
	 *
	 * @param pIdSousTypeProduit : Long :
	 * valeur à passer à <code>this.idSousTypeProduit</code>.
	 */
	void setIdSousTypeProduit(Long pIdSousTypeProduit);



	/**
	 * <div>
	 * <p>Getter du Nom du sous-type de produit comme :</p>
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
	 * <p>Setter du Nom du sous-type de produit comme :</p>
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
	 * <p>Getter du Type de produit auquel est rattaché le présent 
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
	 * <p>Setter du TypeProduitI auquel est rattaché le présent 
	 * SousTypeProduitI.</p>
	 * <p>par exemple "vêtement" pour le sous-type de produit 
	 * "vêtement pour homme".</p>
	 * <ul>
	 * <li>traite le cas d'une mauvaise instance passée en paramètre.</li>
	 * <li>ne fait rien et return si pTypeProduit == this.typeProduit.</li>
	 * <li>détache le présent SousTypeProduit de l’ancien parent 
	 * et le <span style="font-weight:bold;">retire de sa liste</span> 
	 * sousTypeProduits.</li>
	 * <li>passe pTypeProduit à <code>this.typeProduit</code>.</li>
	 * <li>rattache le présent SousTypeProduit au nouveau parent 
	 * et <span style="font-weight:bold;">l'ajoute à la liste</span> 
	 * sousTypeProduits du parent TypeProduitI.</li>
	 * <li>passe this.valide à true si pTypeProduit n'est pas null.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduitI :
	 * valeur à passer à <code>this.typeProduit</code>.
	 */
	void setTypeProduit(TypeProduitI pTypeProduit);



	/**
	 * <div>
	 * <p>Getter de la Collection des produits qualifiés par le présent 
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
	 * @return List&lt;ProduitI&gt; : liste des produits 
	 * (enfants) du présent SousTypeProduitI.
	 */
	List<? extends ProduitI> getProduits();



	/**
	 * <div>
	 * <p style="font-weight:bold;">SETTER CANONIQUE INTELLIGENT</p>
	 * <p>Setter de la Collection des produits qualifiés 
	 * par le présent sous-type de produit.</p>
	 * <p>par exemple : </p>
	 * <ul>
	 * <li>"chemise à manches longues pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"chemise à manches courtes pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"sweat-shirt pour homme" pour le sous-produit "vêtement pour homme"</li>
	 * <p>Techniquement : </p>
	 * <ul>
	 * <li>traite le cas d'une mauvaise instance dans pProduits.</li>
	 * <li>Détache (en mettant leur SousTypeProduitI à null) 
	 * tous les ProduitI enfants de la présente liste 
	 * <code style="font-weight:bold;">this.produits</code> 
	 * en utilisant le Setter canonique de l'enfant ProduitI.</li>
	 * <li>vide la liste <code style="font-weight:bold;">
	 * this.produits</code> avec clear() si pProduits == null. 
	 * Ne jamais créer une nouvelle liste avec new ArrayList() 
	 * pour être Hibernate-safe.</li>
	 * <li>attache les nouveaux ProduitI enfants contenus 
	 * dans pProduits au présent parent SousTypeProduitI 
	 * en utilisant le Setter canonique de l'enfant ProduitI.</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pProduits : List<Produit> :
	 * valeur à passer à <code>this.produits</code>.
	 */
	void setProduits(List<? extends ProduitI> pProduits);


}
