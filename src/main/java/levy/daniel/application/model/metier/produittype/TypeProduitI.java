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
 * <p style="font-weight:bold;">INTERFACE TypeProduitI :</p>
 * <p>Interface factorisant les comportements communs 
 * des objets métier <span style="font-weight:bold;">TypeProduit</span> 
 * (modélisant un "vêtement", "outillage", "logiciel"...) 
 * et de leurs Entities 
 * (par exemple <span style="font-weight:bold;">TypeProduitJPA</span>)</p>
 * </div>
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 11 janvier 2026
 */
public interface TypeProduitI extends Comparable<TypeProduitI>
								, IExportateurCsv, IExportateurJTable {

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">clone profond de manière Thread-Safe un 
	 * <code>TypeProduitI</code> en utilisant 
	 * un {@link CloneContext} pour garantir la cohérence des données</p>
	 * </div>
	 *
	 * @return TypeProduitI : clone profond.
	 */
	TypeProduitI cloneDeep();

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">retourne un Clone PROFOND 
	 * du présent TypeProduitI</p>
	 * <ul>
	 * <li>vérifie que le clone n'existe pas déjà dans l'IdentityHashMap 
	 * (cache)
	 * du {@link CloneContext}. Le cas échéant, 
	 * retourne le clone déjà existant.</li>
	 * <li>instancie un clone parent "nu" cloneTP sans enfants.</li>
	 * <li>rajoute cloneTP dans le cache du CloneContext.</li>
	 * <li>Clone les enfants de la liste sousTypeProduits 
	 * si elle n'est pas null.</li>
	 * <li>clone profond l'enfant SousTypeProduit.</li>
	 * <li>recolle le clone profond du SousTypeProduit au 
	 * TypeProduit parent cloneTP via le Setter canonique 
	 * qui l'ajoute dans cloneTP.sousTypeProduits.</li>
	 * </ul>
	 * </div>
	 *
	 * @param ctx : CloneContext.
	 * @return TypeProduitI : clone profond.
	 */
	TypeProduitI deepClone(CloneContext ctx);
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">clone un 
	 * <code>TypeProduitI</code> sans ses "enfants", 
	 * c'est à dire sans sa List&lt;SousTypeProduitI&gt; 
	 * <code style="font-weight:bold;">this.sousTypeProduits</code>.</p>
	 * </div>
	 *
	 * @return TypeProduitI : Clone sans enfants.
	 */
	TypeProduitI cloneWithoutChildren();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * rattache l'enfant {@code SousTypeProduitI} pEnfant 
	 * au présent parent {@code TypeProduit} de manière Thread-safe.
	 * </p>
	 * <p>Utilise le SETTER CANONIQUE INTELLIGENT 
	 * {@code setTypeProduit(this)}  de l'enfant 
	 * {@code SousTypeProduitI} pEnfant pour le rattachement.</p>
	 * <ul>
	 * <li>Traite le cas d'une mauvaise instance en paramètre 
	 * via une méthode private des classes concrètes.</li>
	 * <li>Retourne et ne fait rien si pEnfant == null 
	 * ou si le libellé de pEnfant est blank (null ou espaces).</li>
	 * <li>Détermine l'ordre de verrouillage pour éviter les deadlocks.</li>
	 * <li>Synchronize sur this et pEnfant avec un ordre systématique 
	 * pour être Thread-Safe.</li>
	 * <li>Rattache pEnfant au parent this si ce n'est pas déjà fait. 
	 * Sinon, ne fait rien.</li>
	 * <li><code>pSousTypeProduit.setTypeProduit(this)</code> pour rattacher 
	 * l'enfant <code>pSousTypeProduit</code> au parent <code>this</code> 
	 * via le <strong>SETTER CANONIQUE INTELLIGENT</strong> de 
	 * <code>SousTypeProduit</code>, qui :
	 * <ul><li>met à jour <code>pSousTypeProduit.typeProduit</code>,</li>
	 * <li>ajoute automatiquement <code>pSousTypeProduit</code>
	 * à <code>this.sousTypeProduits</code> (bidirectionnalité).</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </div>
	 *
	 * @param pEnfant : {@code SousTypeProduitI} : 
	 * enfant à rattacher au présent parent this.
	 */
	void rattacherEnfantSTP(SousTypeProduitI pEnfant);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * détache l'enfant {@code SousTypeProduitI} pEnfant 
	 * du présent parent {@code TypeProduit} de manière Thread-safe.
	 * </p>
	 * <p>Utilise le SETTER CANONIQUE INTELLIGENT 
	 * {@code setTypeProduit(null)}  de l'enfant 
	 * {@code SousTypeProduitI} pEnfant pour le détachement.</p>
	 * <ul>
	 * <li>Traite le cas d'une mauvaise instance en paramètre 
	 * via une méthode private des classes concrètes.</li>
	 * <li>Retourne et ne fait rien si pEnfant == null 
	 * ou si le libellé de pEnfant est blank (null ou espaces).</li>
	 * <li>ne fait rien et return si la collection this.sousTypeProduits 
	 * dans le présent parent TypeProduitI ne contient 
	 * pas pSousTypeProduit.</li>
	 * <li>Détermine l'ordre de verrouillage pour éviter les deadlocks.</li>
	 * <li>Synchronize sur this et pEnfant avec un ordre systématique 
	 * pour être Thread-Safe.</li>
	 * <li>Détache pEnfant du parent this si nécessaire. 
	 * Sinon, ne fait rien.</li>
	 * <li><code>pSousTypeProduit.setTypeProduit(null)</code> pour détacher 
	 * l'enfant <code>pSousTypeProduit</code> 
	 * du parent <code>this</code> 
	 * via le <strong>SETTER CANONIQUE INTELLIGENT</strong> 
	 * de <code>SousTypeProduit</code>, qui :
	 * <ul>
	 * <li>met à jour <code>pSousTypeProduit.typeProduit</code> 
	 * à <code>null</code>,</li>
	 * <li>retire automatiquement <code>pSousTypeProduit</code> 
	 * de <code>this.sousTypeProduits</code> (bidirectionnalité).</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </div>
	 *
	 * @param pEnfant : {@code SousTypeProduitI} : 
	 * enfant à détacher du présent parent this.
	 */
	void detacherEnfantSTP(SousTypeProduitI pEnfant);
	

	
	 /**
	 * <div>
	 * <p style="font-weight:bold;">
	 * retourne une String affichant <code>this.typeProduit</code>
	 * (libellé)
	 * ou "null" en cas de nullité.</p>
	 * <p>(exemple : "vêtement", "outillage", ...)</p>
	 * </div>
	 *
	 * @return this.typeProduit : String
	 */
	String afficherTypeProduit();

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * retourne une String pour l'affichage du contenu de 
	 * la collection <code>this.sousTypeProduits</code></p>
	 * <p>exemple : Sous-Types de Produit=[vêtement pour 
	 * homme, vêtement pour femme, vêtement pour enfant]</p>
	 * </div>
	 *
	 * @return String
	 */
	String afficherSousTypeProduits();
	

	
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
	 * @param pTypeProduit : TypeProduitI : Objet métier à afficher
	 * @return String
	 */
	String afficherTypeProduitFormate(TypeProduitI pTypeProduit);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter de l'ID dans le stockage du type de produit.</p>
	 * </div>
	 * 
	 * @return <code>this.idTypeProduit</code> : Long
	 */
	Long getIdTypeProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Setter de l'ID dans le stockage du type de produit.</p>
	 * </div>
	 *
	 * @param pIdTypeProduit : Long :
	 * valeur à passer à <code>this.idTypeProduit</code>.
	 */
	void setIdTypeProduit(Long pIdTypeProduit);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du type de produit comme "vêtement", "outillage".</p>
	 * </div>
	 *
	 * @return <code>this.typeProduit</code> : String
	 */
	String getTypeProduit();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Setter du type de produit comme "vêtement", "outillage".</p>
	 * </div>
	 *
	 * @param pTypeProduit : String :
	 * valeur à passer à <code>this.typeProduit</code>.
	 */
	void setTypeProduit(String pTypeProduit);



	/**
	 * <div>
	 *<p style="font-weight:bold;">
	 *Getter de la Liste des enfants sous-types de produit 
	 * du présent type de produit</p>
	 * <p>par exemple, pour le type de produit "vêtement" :</p>
	 * <ul>
	 * <li>vêtement pour homme</li>
	 * <li>vêtement pour femme</li>
	 * <li>vêtement pour enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @return <code>this.sousTypeProduits</code> : 
	 * List&lt;SousTypeProduit&gt;
	 */
	List<? extends SousTypeProduitI> getSousTypeProduits();
	
	
	 
    /**
     * <div>
     * <p style="font-weight:bold;">SETTER CANONIQUE INTELLIGENT</p>
     * <p>Setter de la Liste des sous-types de produit 
	 * du présent type de produit</p>
	 * <p>par exemple, pour le type de produit "vêtement" :</p>
	 * <ul>
	 * <li>vêtement pour homme</li>
	 * <li>vêtement pour femme</li>
	 * <li>vêtement pour enfant</li>
	 * </ul>
	 * <p>Techniquement : </p>
	 * <ul>
	 * <li>Détache (en mettant leur TypeProduitI à null) 
	 * tous les SousTypeProduitI enfants de la présente liste 
	 * <code style="font-weight:bold;">this.sousTypeProduits</code> 
	 * en utilisant le Setter canonique de l'enfant SousTypeProduitI.</li>
	 * <li>vide la liste <code style="font-weight:bold;">
	 * this.sousTypeProduits</code> avec clear() 
	 * si pSousTypeProduits == null. 
	 * Ne jamais faire new ArrayList() pour être Hibernate-safe.</li>
	 * <li>attache les nouveaux SousTypeProduitI enfants contenus 
	 * dans pSousTypeProduits au présent parent TypeProduitI 
	 * en utilisant le Setter canonique de l'enfant SousTypeProduitI.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduits : List&lt;SousTypeProduit&gt; :
	 * valeur à passer à <code>this.sousTypeProduits</code>.
     */
    void setSousTypeProduits(
    		List<? extends SousTypeProduitI> pSousTypeProduits);


    
}
