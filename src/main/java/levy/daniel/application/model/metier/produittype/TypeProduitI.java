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
	 * un {@link CloneContext} pour garantir la cohérence des données.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>fournir un clone profond du présent TypeProduitI.</li>
	 * <li>Créer un clone "nu" (sans enfants) de manière thread-safe.</li>
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
	 * </ul>
	 * </div>
	 *
	 * @return TypeProduitI : clone profond.
	 * @throws CloneNotSupportedException 
	 * si le clonage n'est pas supporté.
	 */
	TypeProduitI clone() throws CloneNotSupportedException;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">clone profond de manière Thread-Safe un 
	 * <code>TypeProduitI</code> en utilisant 
	 * un {@link CloneContext} pour garantir la cohérence des données.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>fournir un clone profond du présent TypeProduitI.</li>
	 * <li>Créer un clone "nu" (sans enfants) de manière thread-safe.</li>
	 * <li>Créer une copie thread-safe de la liste des éventuels enfants 
	 * pour éviter les modifications concurrentes.</li>
	 * <li>Cloner chaque enfant de manière thread-safe 
	 * via {@code enfant.deepClone(CloneContext ctx)} 
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
	 * </ul>
	 * </div>
	 *
	 * @param ctx : CloneContext : 
	 * Gestionnaire des clones encapsulant 
	 * une {@code IdentityHashMap} (cache)
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
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Traiter le cas d'une mauvaise instance en paramètre 
	 * via une méthode private des classes concrètes.</li>
	 * <li>Retourner et ne rien faire si pEnfant == null 
	 * ou si le libellé de pEnfant est blank (null ou espaces).</li>
	 * <li>Déterminer un ordre de verrouillage unique 
	 * pour éviter les deadlocks .</li>
	 * <li>Synchronize sur this puis pEnfant avec cet ordre systématique 
	 * pour être Thread-Safe.</li>
	 * <li>Rattacher pEnfant au parent this si ce n'est pas déjà fait. 
	 * Sinon, ne rien faire.</li>
	 * <li><code>pEnfant.setTypeProduit(this)</code> pour rattacher 
	 * l'enfant <code>pEnfant</code> au parent <code>this</code> 
	 * via le <strong>SETTER CANONIQUE INTELLIGENT</strong> de 
	 * {@code SousTypeProduit}, qui :
	 * <ul><li>met à jour <code>pSousTypeProduit.typeProduit</code>,</li>
	 * <li>ajoute automatiquement <code>pSousTypeProduit</code>
	 * à <code>this.sousTypeProduits</code> (bidirectionnalité).</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Synchronize toujours dans l'ordre 1-Parent, 2-Enfant 
	 * pour éviter les deadlocks.</li>
	 * <li>Garantit la stabilité de la bidirectionnalité.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>méthode Thread-Safe.</li>
	 * <li>Ne fait rien si pEnfant est déjà rattaché au parent this.</li>
	 * <li>Fail-Fast si l'instance pEnfant ne convient pas.</li>
	 * <li>Délègue à une méthode private interne dans les 
	 * classes concrètes le rattachement de pEnfant au parent this.</li>
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
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Traiter le cas d'une mauvaise instance en paramètre 
	 * via une méthode private des classes concrètes.</li>
	 * <li>Retourner et ne rien faire si pEnfant == null 
	 * ou si le libellé de pEnfant est blank (null ou espaces).</li>
	 * <li>Déterminer un ordre de verrouillage unique 
	 * pour éviter les deadlocks .</li>
	 * <li>Synchronize sur this puis pEnfant avec cet ordre systématique 
	 * pour être Thread-Safe.</li>
	 * <li>Détacher pEnfant du parent this si ce n'est pas déjà fait. 
	 * Sinon, ne rien faire.</li>
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
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>Synchronize toujours dans l'ordre 1-Parent, 2-Enfant 
	 * pour éviter les deadlocks.</li>
	 * <li>Garantit la stabilité de la bidirectionnalité.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	 * <ul>
	 * <li>méthode Thread-Safe.</li>
	 * <li>Ne fait rien si pEnfant n'est pas 
	 * déjà rattaché au parent this.</li>
	 * <li>Fail-Fast si l'instance pEnfant ne convient pas.</li>
	 * <li>Délègue à une méthode private interne dans les 
	 * classes concrètes le détachement de pEnfant du parent this.</li>
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
     * <p><b>Getter de la liste des enfants SousTypeProduit 
     * associés au présent TypeProduit.</b></p>
     * <p>Par exemple, pour le type de produit "vêtement" :</p>
     * <ul>
     * <li>vêtement pour homme</li>
     * <li>vêtement pour femme</li>
     * <li>vêtement pour enfant</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p><b>CONTRAT TECHNIQUE :</b></p>
     * <ul>
     * <li>retourne une <b>copie défensive immuable (snapshot)</b>
     * de la liste interne des SousTypeProduit associés à ce TypeProduit.</li>
     * <li>la liste retournée reflète l'état exact des sous-types
     * au moment de l'appel.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p><b>GARANTIES TECHNIQUES et METIER :</b></p>
     * <ul>
     * <li>méthode Thread-Safe.</li>
     * <li>aucune modification externe ne peut altérer l'état interne.</li>
     * <li>aucune incohérence concurrente ne peut survenir pendant la lecture.</li>
     * </ul>
     * </div>
     *
     * @return List&lt;? extends SousTypeProduitI&gt; :
     * copie immuable de la liste des SousTypeProduit,
     * pouvant être vide mais jamais null.
     */
	List<? extends SousTypeProduitI> getSousTypeProduits();
	
	
	 
    /**
     * <div>
     * <p><b>SETTER CANONIQUE INTELLIGENT</b></p>
     * <p>met à jour la liste des SousTypeProduit enfants 
     * du présent TypeProduit.</p>
     * </div>
     *
     * <div>
     * <p><b>INTENTION TECHNIQUE (scénario nominal) :</b></p>
     * <ul>
     * <li>traite le cas d'une mauvaise instance en paramètre 
     * via une méthode private implémentée dans les classes concrètes.</li>
     * <li>synchronise sur le parent (this).</li>
     * <li>détache tous les enfants actuels via le setter 
     * canonique de l'enfant.</li>
     * <li>rattache les nouveaux enfants via le setter 
     * canonique de l'enfant.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p><b>CONTRAT TECHNIQUE :</b></p>
     * <ul>
     * <li>si pSousTypeProduits == null : 
     * vide la liste et détache tous les enfants.</li>
     * <li>ignore les éléments null dans pSousTypeProduits.</li>
     * <li>doit être thread-safe et 
     * préserver la bidirectionnalité parent/enfant.</li>
     * </ul>
     * </div>
     *
     * @param pSousTypeProduits : List&lt;? extends SousTypeProduitI&gt; :
     * nouvelle liste d'enfants à rattacher, ou null pour vider/détacher.
     */

    void setSousTypeProduits(
    		List<? extends SousTypeProduitI> pSousTypeProduits);


    
}
