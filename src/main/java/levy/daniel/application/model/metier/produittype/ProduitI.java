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
	 * <p><span style="font-weight:bold;">clone profond</span> 
	 * un <code>ProduitI</code> en utilisant 
	 * un CloneContext pour garantir la cohérence des données</p>
	 * </div>
	 *
	 * @return ProduitI : clone profond
	 */
	ProduitI cloneDeep();



	/**
	 * <div>
	 * <p style="font-weight:bold;">retourne un Clone PROFOND 
	 * du présent ProduitI</p>
	 * <ul>
	 * <li>vérifie que le clone n'existe pas déjà dans l'IdentityHashMap 
	 * (cache)
	 * du CloneContext. Le cas échéant, 
	 * retourne le clone déjà existant.</li>
	 * <li>instancie un clone "nu" cloneP sans parent.</li>
	 * <li>rajoute le clone "nu" cloneP dans le cache du CloneContext.</li>
	 * <li>Clone le parent SousTypeProduit (si présent) et recolle 
	 * le clone parent au CloneP via le Setter canonique.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pCtx : CloneContext
	 * @return ProduitI : clone profond
	 */
	ProduitI deepClone(CloneContext ctx);
	
	
	
	/**
	 * <div>
	 * <p>clone un ProduitI sans "parent" SousTypeProduitI.</p>
	 * </div>
	 *
	 * @return ProduitI : clone sans parent.
	 */
	ProduitI cloneWithoutParent();

	
	/**
	 * <div>
	 * <p>retourne une String affichant <code>this.produit</code>
	 * (libellé) ou "null" en cas de nullité.</p>
	 * <p>(exemple : "chemise à manches longues pour homme", ...).</p>
	 * </div>
	 *
	 * @return String : this.produit
	 */
	String afficherProduit();

	
	/**
	 * <div>
	 * <p>Getter du Type de Produit du présent PRODUIT 
	 * stocké dans <code>this.sousTypeProduit</code>.</p>
	 * <p>Doit être calculé et jamais serializé.</p>
	 * </div>
	 *
	 * @return <code>this.typeProduit</code> : TypeProduit
	 */
	TypeProduitI getTypeProduit();
	

	
	/**
	 * <div>
	 * <p>Getter du Boolean qui indique si le présent Produit 
	 * possède un SousTypeProduit non null.</p>
	 * <ul>
	 * <li>true si le présent Produit possède un SousTypeProduit non null.</li>
	 * </ul>
	 * <p>Doit être calculé et jamais serializé.</p>
	 * </div>
	 *
	 * @return <code>this.valide</code> : Boolean
	 */
	boolean isValide();



	/**
	 * <div>
	 * <p>Getter de l'ID en base du produit.</p>
	 * </div>
	 *
	 * @return <code>this.idProduit</code> : Long
	 */
	Long getIdProduit();



	/**
	 * <div>
	 * <p>Setter de l'ID en base du produit.</p>
	 * </div>
	 *
	 * @param pIdProduit : Long :
	 * valeur à passer à <code>this.idProduit</code>.
	 */
	void setIdProduit(Long pIdProduit);



	/**
	 * <div>
	 *<p>Getter du Nom du produit comme par exemple :</p>
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
	 * <p>Setter du Nom du produit comme par exemple :</p>
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
	 * <p>Getter du sous-type de produit qui caractérise le présent produit.</p>
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
	 * le présent produit.</p>
	 * <p>par exemple : "vêtement pour homme" pour un PRODUIT 
	 * "tee-shirt pour homme".</p>
	 * <ul>
	 * <li>traite le cas d'une mauvaise instance de pSousTypeProduit.</li>
	 * <li>mémorise l'ancienne valeur de this.sousTypeProduit.</li>
	 * <li>ne fait rien et return si la valeur passée en paramètre 
	 * vaut l'ancienne valeur.</li>
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
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitI :
	 * valeur à passer à <code>this.sousTypeProduit</code>.
	 */
	void setSousTypeProduit(SousTypeProduitI pSousTypeProduit);

	
}
