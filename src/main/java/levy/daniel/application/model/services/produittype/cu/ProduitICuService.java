/* ********************************************************************* */
/* ********************* PORT SERVICE CU ******************************* */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu;

import java.util.List;

import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <p style="font-weight:bold;">INTERFACE ProduitICuService.java :</p>
 *
 * <p>Cette INTERFACE modélise :</p>
 * <p><span style="font-weight:bold;">les SERVICES METIER (CU)</span>
 * pour l'objet Métier <code style="font-weight:bold;">
 * Produit</code>.</p>
 * <p style="font-weight:bold;">PORT SERVICE USE CASE</p>
 *
 * <p>
 * Utilisée par la couche de présentation CONTROLLER pour :
 * </p>
 * <ul>
 * <li>créer / modifier un Objet métier 
 * via {@link #creer(Object)}</li>
 * <li>lister toutes les Objets métier 
 * via {@link #rechercherTous()}</li>
 * <li>lister toutes les Objets métier par pages via
 * {@link #rechercherTousParPage(RequetePage)}</li>
 * <li>rechercher par libellé exact 
 * via {@link #findByLibelle(String)}</li>
 * <li>rechercher par libellé contenant 
 * via {@link #findByLibelleRapide(String)}</li>
 * <li>rechercher par DTO 
 * via {@link #findByDTO(SousTypeProduitDTO.InputDTO)}</li>
 * <li>rechercher par ID 
 * via {@link #findById(Long)}</li>
 * <li>supprimer via {@link #delete(SousTypeProduitDTO.InputDTO)}</li>
 * <li>compter via {@link #count()}</li>
 * <li>émettre des messages utilisateurs via {@link #getMessage()}</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
public interface ProduitICuService {

	//* -----------------------CONSTANTES ------------------------------*//

	/**
	 * Chaîne " - ".
	 */
	String TIRET_ESPACE = " - ";

	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Produit null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NULL 
		= "vous ne pouvez pas sauvegarder un Produit null.";

	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Produit 
	 * dont le libellé est blank."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NOM_BLANK 
	= "vous ne pouvez pas sauvegarder un Produit "
			+ "dont le libellé est blank.";

	/**
	 * <div>
	 * <p>"OK - La création de l'objet s'est bien déroulée."</p>
	 * </div>
	 */
	String MESSAGE_CREER_OK 
		= "OK - La création de l'objet s'est bien déroulée.";

	/**
	 * <div>
	 * <p>"Vous ne pouvez pas sauvegarder un Produit 
	 * déjà existant dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_DOUBLON 
		= "Vous ne pouvez pas sauvegarder Produit "
				+ "déjà existant dans le stockage : ";

	/**
	 * <div>
	 * <p>"Le stockage n'a pas retourné d'enregistrements (null)."</p>
	 * </div>
	 */
	String MESSAGE_STOCKAGE_NULL
		= "Le stockage n'a pas retourné d'enregistrements (null).";
	
	/**
	 * <div>
	 * <p>"l'indication de page demandée ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_PAGEABLE_NULL
		= "l'indication de page demandée ne doit pas être null.";

	/**
	 * <div>
	 * <p>"Le paramètre ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_PARAM_NULL
		= "Le paramètre ne doit pas être null.";

	/**
	 * <div>
	 * <p>"Vous avez passé une chaine de caractères blank
	 * (null ou que des espaces) en paramètre."</p>
	 * </div>
	 */
	String MESSAGE_PARAM_BLANK = "Vous avez passé une chaine "
			+ "de caractères blank (null ou que des espaces) en paramètre.";

	/**
	 * <div>
	 * <p>"Erreur lors de la création de l'objet".</p>
	 * </div>
	 */
	String MESSAGE_CREER_KO = "Erreur lors de la création de l'objet";

	/**
	 * <div>
	 * <p>"Aucun enregistrement ne correspond à la recherche".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_VIDE = "Aucun enregistrement ne correspond à la recherche";

	/**
	 * <div>
	 * <p>"La recherche a retourné des enregistrements".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OK = "La recherche a retourné des enregistrements";

	/**
	 * <div>
	 * <p>"Le Produit est null".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OBJ_NULL = "Le Produit est null";

	/**
	 * <div>
	 * <p>"La recherche a abouti".</p>
	 * </div>
	 */
	String MESSAGE_SUCCES_RECHERCHE = "La recherche a abouti";

	/**
	 * <div>
	 * <p>"Objet Introuvable : ".</p>
	 * </div>
	 */
	String MESSAGE_OBJ_INTROUVABLE = "Objet Introuvable : ";

	/**
	 * <div>
	 * <p>"Objet non persisté en base : ".</p>
	 * </div>
	 */
	String MESSAGE_OBJ_NON_PERSISTE = "Objet non persisté en base : ";

	/**
	 * <div>
	 * <p>"La recherche paginée a abouti".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_OK = "La recherche paginée a abouti";

	/**
	 * <div>
	 * <p>"la recherche paginée a échoué".</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_KO = "la recherche paginée a échoué";

	/**
	 * <div>
	 * <p>"La modification a été effectuée".</p>
	 * </div>
	 */
	String MESSAGE_MODIF_OK = "La modification a été effectuée";

	/**
	 * <div>
	 * <p>"La modification a échouée".</p>
	 * </div>
	 */
	String MESSAGE_MODIF_KO = "La modification a échouée";

	/**
	 * <div>
	 * <p>"La suppression a été effectuée".</p>
	 * </div>
	 */
	String MESSAGE_DELETE_OK = "La suppression a été effectuée";

	/**
	 * <div>
	 * <p>"La suppression a échouée".</p>
	 * </div>
	 */
	String MESSAGE_DELETE_KO = "La suppression a échouée";

	/**
	 * <div>
	 * <p>"Le SousTypeProduit parent ne doit pas être null".</p>
	 * </div>
	 */
	String RECHERCHE_SOUSTYPEPRODUIT_NULL = "Le SousTypeProduit parent ne doit pas être null";

	/**
	 * <div>
	 * <p>"Le SousTypeProduit ne doit pas être null".</p>
	 * </div>
	 */
	String MESSAGE_SOUSTYPEPRODUIT_NULL = "Le SousTypeProduit ne doit pas être null";

	/**
	 * <div>
	 * <p>"Le Produit doit posséder un parent (SousTypeProduit)".</p>
	 * </div>
	 */
	String MESSAGE_PAS_PARENT = "Le Produit doit posséder un parent (SousTypeProduit)";

	/**
	 * <div>
	 * <p>"Une recherche technique a échouée".</p>
	 * </div>
	 */
	String KO_TECHNIQUE_RECHERCHE = "Une recherche technique a échouée";

	// * ------------------------METHODES -------------------------------*//

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Stocke un {@link Produit}
	 * dans le stockage en déléguant à un composant technique GATEWAY
	 * et le retourne sous forme d'OutputDTO.
	 * </p>
	 * <ul>
	 * <li>reçoit un InputDTO.</li>
	 * <li>convertit l'InputDTO -> Objet métier.</li>
	 * <li>appelle le SERVICE GATEWAY pour l'opération sur le stockage.</li>
	 * <li>récupère l'objet métier retourné par le GATEWAY.</li>
	 * <li>convertit l'objet métier -> OutputDTO.</li>
	 * <li>retourne OutputDTO.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_CREER_NULL}
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si {@code pInputDTO.getProduit()} est Blank
	 * , positionne {@link #getMessage()}
	 * à {@link #MESSAGE_CREER_NOM_BLANK}, LOG et lève une exception.</li>
	 * <li>Si le DTO est un doublon, positionne {@link #getMessage()} à
	 * {@link #MESSAGE_DOUBLON} + libellé et lève une exception.</li>
	 * <li>Si le parent {@link SousTypeProduit} est manquant/absent,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT}
	 * et lève une exception.</li>
	 * <li>Sinon, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_CREER_OK}, délègue la création
	 * au composant technique GATEWAY et retourne
	 * l'OutputDTO correspondant à l'objet stocké.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO :
	 * le Produit à stocker.
	 * @return ProduitDTO.OutputDTO :
	 * le Produit sauvegardé dans le stockage.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO creer(
			ProduitDTO.InputDTO pInputDTO) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les
	 * <code>{@link Produit}</code>
	 * présents dans le stockage sous forme de DTOs
	 * <code>OutputDTO</code>.
	 * </p>
	 * <ul>
	 * <li>demande au SERVICE GATEWAY la liste de tous les
	 * enregistrements présents dans le stockage.</li>
	 * <li>émet un message, Log.info et jette une ExceptionStockageVide
	 * si la liste retournée est null.</li>
	 * <li>convertit les objets métier retournés en OutputDTO
	 * , fabrique la liste et la retourne.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si le stockage retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL}
	 * et lève une exception.</li>
	 * <li>Si la liste retournée est vide, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}
	 * et retourne la liste vide.</li>
	 * <li>Sinon, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OK} et retourne la liste.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List&lt;ProduitDTO.OutputDTO&gt; : liste de tous les Produits.
	 * @throws Exception
	 */
	List<ProduitDTO.OutputDTO> rechercherTous() throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les libellés des {@link Produit}
	 * présents dans le stockage sous forme d'une liste de String.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si le stockage retourne {@code null}, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_STOCKAGE_NULL}
	 * et lève une exception.</li>
	 * <li>Si aucun libellé non blank n'est trouvé, positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}
	 * et retourne une liste vide.</li>
	 * <li>Sinon, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_OK} et retourne la liste des libellés.</li>
	 * </ul>
	 * </div>
	 *
	 * @return List&lt;String&gt; : liste des libellés.
	 * @throws Exception
	 */
	List<String> rechercherTousString() throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link Produit} présents dans le stockage
	 * de manière paginée, sous forme de {@link ResultatPage} de OutputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pRequetePage == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PAGEABLE_NULL} et lève une exception.</li>
	 * <li>Si le stockage retourne {@code null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_PAGINEE_KO} et retourne {@code null}.</li>
	 * <li>Sinon, positionne {@link #getMessage()} à
	 * {@link #MESSAGE_RECHERCHE_PAGINEE_OK} et retourne le {@link ResultatPage}
	 * converti.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage RequetePage : requête de pagination.
	 * @return ResultatPage&lt;ProduitDTO.OutputDTO&gt; : page de résultats.
	 * @throws Exception
	 */
	ResultatPage<ProduitDTO.OutputDTO> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link Produit} dans le stockage à partir de son libellé.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est Blank, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK}
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si aucun objet n'est trouvé, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Sinon, retourne l'OutputDTO correspondant et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle String : libellé du Produit.
	 * @return ProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO findByLibelle(String pLibelle) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche "rapide" des {@link Produit} dans le stockage à partir d'un préfixe de libellé.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pContenu} est {@code null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL} et lève une exception.</li>
	 * <li>Si {@code pContenu} est Blank, délègue à {@link #rechercherTous()}
	 * (mêmes messages/erreurs que {@code rechercherTous}).</li>
	 * <li>Si la recherche retourne une liste vide, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_RECHERCHE_VIDE} et retourne la liste vide.</li>
	 * <li>Sinon, positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}
	 * et retourne la liste des résultats.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu String : contenu (préfixe) de recherche rapide.
	 * @return List&lt;ProduitDTO.OutputDTO&gt; : liste de DTOs résultats (non null).
	 * @throws Exception
	 */
	List<ProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link Produit} dans le stockage à partir d'un InputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, retourne {@code null}
	 * et positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OBJ_NULL}
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si {@code pInputDTO.getSousTypeProduit()} est Blank,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT}
	 * et lève une exception.</li>
	 * <li>Si aucun objet n'est trouvé, retourne {@code null}
	 * et positionne {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Sinon, retourne l'OutputDTO correspondant et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO ProduitDTO.InputDTO : DTO de recherche.
	 * @return ProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO findByDTO(ProduitDTO.InputDTO pInputDTO) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les {@link Produit} attachés à un parent {@link SousTypeProduit},
	 * sous forme d'OutputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pSousTypeProduit == null}, positionne {@link #getMessage()}
	 * à {@link #RECHERCHE_SOUSTYPEPRODUIT_NULL} et lève une exception.</li>
	 * <li>Si le parent {@link SousTypeProduit} n'est pas trouvé/persistant,
	 * positionne {@link #getMessage()} à {@link #MESSAGE_PAS_PARENT}
	 * et lève une exception.</li>
	 * <li>Si aucun {@link Produit} n'est trouvé, retourne une liste vide et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Sinon, retourne la liste des OutputDTO et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit SousTypeProduitDTO.InputDTO : parent.
	 * @return List&lt;ProduitDTO.OutputDTO&gt; : liste des Produits (non null).
	 * @throws Exception
	 */
	List<ProduitDTO.OutputDTO> findAllByParent(
			SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link Produit} dans le stockage à partir de son identifiant.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pId == null}, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL}
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si aucun objet n'est trouvé, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + id.</li>
	 * <li>Sinon, retourne l'OutputDTO correspondant et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId Long : identifiant.
	 * @return ProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO findById(Long pId) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Met à jour un {@link Produit} existant à partir d'un InputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL} et lève une exception.</li>
	 * <li>Si {@code pInputDTO.getProduit()} est Blank, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK} et lève une exception.</li>
	 * <li>Si l'objet est introuvable, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si l'objet est non persistant, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé et lève une exception.</li>
	 * <li>Si la modification échoue, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_MODIF_KO} + libellé.</li>
	 * <li>Sinon, retourne l'OutputDTO modifié et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_MODIF_OK} + parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO ProduitDTO.InputDTO : DTO de modification.
	 * @return ProduitDTO.OutputDTO : DTO modifié ou null.
	 * @throws Exception
	 */
	ProduitDTO.OutputDTO update(ProduitDTO.InputDTO pInputDTO) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Supprime un {@link Produit} à partir d'un InputDTO.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL} et lève une exception.</li>
	 * <li>Si {@code pInputDTO.getProduit()} est Blank, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_BLANK} et lève une exception.</li>
	 * <li>Si l'objet est introuvable, ne supprime rien et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si la suppression échoue, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_DELETE_KO} + libellé et lève une exception.</li>
	 * <li>Sinon, supprime et positionne {@link #getMessage()}
	 * à {@link #MESSAGE_DELETE_OK} + libellé.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO ProduitDTO.InputDTO : DTO de suppression.
	 * @throws Exception
	 */
	void delete(ProduitDTO.InputDTO pInputDTO) throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne le nombre total de {@link Produit} présents en stockage.
	 * </p>
	 * </div>
	 *
	 * @return long : nombre de Produits.
	 * @throws Exception
	 */
	long count() throws Exception;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne le dernier message métier/observable produit par le service.
	 * </p>
	 * </div>
	 *
	 * @return String : message courant (peut être null selon le contexte).
	 */
	String getMessage();

}
