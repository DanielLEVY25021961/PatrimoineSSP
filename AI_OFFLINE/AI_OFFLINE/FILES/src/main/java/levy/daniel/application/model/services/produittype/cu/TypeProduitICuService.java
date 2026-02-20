/* ********************************************************************* */
/* ********************* PORT SERVICE CU ******************************* */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu;

import java.util.List;

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">INTERFACE TypeProduitICuService.java :</p>
 * 
 * <p>Cette INTERFACE modélise :</p> 
 * <p><span style="font-weight:bold;">les SERVICES METIER (CU)</span> 
 * pour l'objet Métier <code style="font-weight:bold;">
 * TypeProduit</code>.</p>
 * <p style="font-weight:bold;">PORT SERVICE USE CASE</p>
 * 
 * <p>
 * Utilisée par la couche de présentation CONTROLLER pour :
 * </p>
 * <ul>
 * <li>créer / modifier un Objet métier via {@link #creer(Object)}</li>
 * <li>lister toutes les Objets métier via {@link #rechercherTous()}</li>
 * <li>lister toutes les Objets métier par pages via 
 * {@link #rechercherTousParPage(RequetePage)}</li>
 * <li>rechercher par libellé exact via {@link #findByLibelle(String)}</li>
 * <li>rechercher par libellé contenant via {@link #findByLibelleRapide(String)}</li>
 * <li>rechercher par ID via {@link #findById(Object)}</li>
 * <li>supprimer via {@link #delete(Object)}</li>
 * <li>compter via {@link #count()}</li>
 * <li>émettre des messages utilisateurs via {@link #getMessage()}</li>
 * </ul>
 * </div>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 janvier 2026
 */
public interface TypeProduitICuService {

	//* ------------------------ATTRIBUTS -------------------------------*//
	/**
	 * <div>
	 * <p>" - "</p>
	 * </div>
	 */
	String TIRET_ESPACE = " - ";
	
	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Type de Produit null."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NULL 
		= "vous ne pouvez pas sauvegarder un Type de Produit null.";

	/**
	 * <div>
	 * <p>"vous ne pouvez pas sauvegarder un Type de Produit 
	 * dont le libellé est blank (null ou que des espaces)."</p>
	 * </div>
	 */
	String MESSAGE_CREER_NOM_BLANK 
		= "vous ne pouvez pas sauvegarder un Type de Produit "
			+ "dont le libellé est blank (null ou que des espaces).";
	
	/**
	 * <div>
	 * <p>"OK - La création de l'objet s'est bien déroulée."</p>
	 * </div>
	 */
	String MESSAGE_CREER_OK 
		= "OK - La création de l'objet s'est bien déroulée.";

	/**
	 * <div>
	 * <p>"Vous ne pouvez pas sauvegarder un Type de Produit 
	 * déjà existant dans le stockage : "</p>
	 * </div>
	 */
	String MESSAGE_DOUBLON 
		= "Vous ne pouvez pas sauvegarder un Type de Produit "
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
	 * <p>"Vous avez passé null en paramètre."</p>
	 * </div>
	 */
	String MESSAGE_PARAM_NULL 
		= "Vous avez passé null en paramètre.";
	
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
	 * <p>"Impossible de trouver dans le stockage l'objet : "</p>
	 * </div>
	 */
	String MESSAGE_OBJ_INTROUVABLE 
		= "Impossible de trouver dans le stockage l'objet : ";
	
	/**
	 * <div>
	 * <p>"Impossible de modifier - 
	 * l'objet n'est pas persistant (pas d'ID dans le stockage) : "</p>
	 * </div>
	 */
	String MESSAGE_OBJ_NON_PERSISTE 
		= "Impossible de modifier - "
			+ "l'objet n'est pas persistant (pas d'ID dans le stockage) : ";
	
	/**
	 * <div>
	 * <p>"Erreur non spécifiée"</p>
	 * </div>
	 */
	String MSG_ERREUR_NON_SPECIFIEE = "Erreur non spécifiée";

	/**
	 * <div>
	 * <p>"l'objet à rechercher ne doit pas être null."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OBJ_NULL 
		= "l'objet à rechercher ne doit pas être null.";

	/**
	 * <div>
	 * <p>"La recherche a bien retourné un objet."</p>
	 * </div>
	 */
	String MESSAGE_SUCCES_RECHERCHE 
		= "La recherche a bien retourné un objet.";
	
	/**
	 * <div>
	 * <p>"La recherche n'a retourné aucun résutat."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_VIDE 
		= "La recherche n'a retourné aucun résutat.";
	
	/**
	 * <div>
	 * <p>"OK - La recherche a retourné des résultats."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_OK 
		= "OK - La recherche a retourné des résultats.";
	
	/**
	 * <div>
	 * <p>"KO - la recherche paginée a retourné null."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_KO 
		= "KO - la recherche paginée a retourné null.";
	
	/**
	 * <div>
	 * <p>"OK - la recherche paginée a retourné des résultats."</p>
	 * </div>
	 */
	String MESSAGE_RECHERCHE_PAGINEE_OK 
		= "OK - la recherche paginée a retourné des résultats.";
	
	/**
	 * <div>
	 * <p>"KO - la modification a retourné null : "</p>
	 * </div>
	 */
	String MESSAGE_MODIF_KO = "KO - la modification a retourné null : ";
	
	/**
	 * <div>
	 * <p>"OK - modification réussie de : "</p>
	 * </div>
	 */
	String MESSAGE_MODIF_OK = "OK - modification réussie de : ";
	
	/**
	 * <div>
	 * <p>"OK - destruction réussie de : "</p>
	 * </div> 
	 */
	String MESSAGE_DELETE_OK = "OK - destruction réussie de : ";
		
	/**
	 * <div>
	 * <p>"KO - échec de la destruction de : "</p>
	 * </div> 
	 */
	String MESSAGE_DELETE_KO = "KO - échec de la destruction de : ";

	/**
	 * <div>
	 * <p>"Une recherche technique a échouée".</p>
	 * </div>
	 */
	String KO_TECHNIQUE_RECHERCHE
		= "Une recherche technique a échouée";


	

	//* ------------------------ METHODES -------------------------------*//


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Stocke un {@link TypeProduit}
	 * dans le stockage en déléguant à un composant technique GATEWAY 
	 * et le retourne sous forme d'OutputDTO.
	 * </p>
	 * <ul>
	 * <li>retourne null si pInputDTO == null.</li>
	 * <li>doit jeter une Exception si 
	 * pInputDTO.getTypeProduit() est Blank.</li>
	 * <li>positionne {@link #getMessage()} à {@link #MESSAGE_CREER_NULL}
	 * si pInputDTO est null (aucun LOG, aucune exception).</li>
	 * <li>émet un message, Log.info et jette une 
	 * ExceptionParametreBlank si le libellé dans le DTO est blank.</li>
	 * <li>émet un message, Log.info et jette une ExceptionDoublon 
	 * si le DTO est un doublon.</li>
	 * <li>convertit l'Input DTO en Objet métier.</li>
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
	 * <li>Si {@code pInputDTO.getTypeProduit()} est Blank
	 * , positionne {@link #getMessage()}
	 * à {@link #MESSAGE_CREER_NOM_BLANK}, LOG et lève une exception.</li>
	 * <li>Si le DTO est un doublon, positionne {@link #getMessage()} à
	 * {@link #MESSAGE_DOUBLON} + libellé, LOG et lève une exception.</li>
	 * <li>Sinon, positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_CREER_OK}, délègue la création 
	 * au composant technique GATEWAY et retourne
	 * l'OutputDTO correspondant à l'objet stocké.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO : 
	 * le Type de Produit à stocker.
	 * @return TypeProduitDTO.OutputDTO : 
	 * le Type de Produit sauvegardé dans le stockage.
	 */
	TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO) 
			throws Exception;


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">retourne tous les 
	 * <code>{@link TypeProduit}</code> 
	 * présents dans le stockage sous forme de DTOs 
	 * <code>TypeProduitDTO.OutputDTO</code>.
	 * </p>
	 * <ul>
	 * <li>demande au SERVICE GATEWAY la liste de tous les 
	 * enregistrements dans le stockage.</li>
	 * <li>émet un message, LOG et jette une ExceptionStockageVide 
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
	 * {@link #getMessage()}
	 * à {@link #MESSAGE_STOCKAGE_NULL}, LOG et lève une exception.</li>
	 * <li>Sinon, retourne la liste des OutputDTO correspondant 
	 * à tous les enregistrements
	 * du stockage (dédoublonnés côté CU si nécessaire).</li>
	 * <ul>
	 * <li>si la liste résultat est vide, positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>si la liste résultat n'est pas vide, positionne 
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @return List&lt;TypeProduitDTO.OutputDTO&gt;: 
	 * Liste de tous les objets métier dans le stockage. Jamais null.
	 * @throws Exception 
	 */
	List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne tous les libellés des
	 * <code>{@link TypeProduit}</code>
	 * présents dans le stockage sous forme de 
	 * <code>String</code>.
	 * </p>
	 * <ul>
	 * <li>demande au SERVICE GATEWAY la liste de tous les 
	 * enregistrements dans le stockage.</li>
	 * <li>émet un message, LOG et jette une ExceptionStockageVide 
	 * si la liste retournée est null.</li>
	 * <li>convertit les objets métier retournés en String  
	 * , fabrique la liste et la retourne.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si le stockage retourne {@code null}
	 * , positionne {@link #getMessage()}
	 * à {@link #MESSAGE_STOCKAGE_NULL}, LOG et lève une exception.</li>
	 * <li>Sinon, retourne la liste des libellés 
	 * non blank des objets du stockage
	 * (dédoublonnés côté CU si nécessaire).</li>
	 * <ul>
	 * <li>si la liste résultat est vide, positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>si la liste résultat n'est pas vide, positionne 
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @return List&lt;String&gt; : 
	 * Liste de tous les libellés des objets métier dans le stockage.
	 * @throws Exception
	 */
	List<String> rechercherTousString() throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">retourne tous les 
	 * <code>{@link TypeProduit}</code> 
	 * présents dans le stockage sous forme de DTOs 
	 * <code>TypeProduitDTO.OutputDTO</code> par Page.
	 * </p>
	 * <ul>
	 * <li>émet un message, LOG et jette une 
	 * IllegalStateException si pRequetePage == null.</li>
	 * <li>délègue au Service Gateway la tâche 
	 * de retourner le résultat paginé.</li>
	 * <li>retourne null si resultatPagine == null.</li>
	 * <li>construit un ResultatPage<OutputDTO> en reprenant 
	 * totalElements du résultat Gateway 
	 * (pagination cohérente : totalPages, hasNext…)</li>
	 * <li>convertit les objets metier en OutputDTO.</li>
	 * <li>récupère le message auprès du Gateway 
	 * et retourne le résultat paginé.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pRequetePage == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PAGEABLE_NULL}, LOG et lève une exception.</li>
	 * <li>Si le résultat paginé retourné par le Gateway est {@code null}, 
	 * positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_RECHERCHE_PAGINEE_KO} et retourne {@code null}.</li>
	 * <li>Sinon, retourne un {@link ResultatPage} d'OutputDTO en reprenant
	 * {@code totalElements} du résultat Gateway (pagination cohérente).
	 *  Positionne {@link #getMessage()} 
	 *  à {@link #MESSAGE_RECHERCHE_PAGINEE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage : RequetePage
	 * @return ResultatPage&lt;TypeProduitDTO.OutputDTO&gt; 
	 * : page de résultats.
	 * @throws Exception
	 */
	ResultatPage<TypeProduitDTO.OutputDTO> rechercherTousParPage(
			RequetePage pRequetePage) throws Exception;
	

		
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un {@link TypeProduit}
	 * dans le stockage par libellé exact.</p>
	 * <ul>
	 * <li>retourne null si pTypeProduit est blank (null ou espaces).</li>
	 * <li>retourne null si aucun objet n'est trouvé.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pLibelle} est Blank, retourne 
	 * {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK} 
	 * (aucun LOG, aucune exception).</li>
	 * <li>Si aucun objet n'est trouvé en stockage
	 * , retourne {@code null} et positionne
	 * {@link #getMessage()} à 
	 * {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Sinon, positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_SUCCES_RECHERCHE} 
	 * et retourne l'OutputDTO correspondant à l'objet trouvé.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String : libellé exact à rechercher.
	 * @return TypeProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	TypeProduitDTO.OutputDTO findByLibelle(
			String pLibelle) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne la List&lt;TypeProduitDTO.OutputDTO&gt; 
	 * des {@link TypeProduit}
	 * dont le libellé contient pContenu.
	 * </p>
	 * <ul>
	 * <li>doit jeter une Exception si pContenu est null.</li>
	 * <li>doit retourner tous les enregistrements 
	 * si pContenu est blank.</li>
	 * <li>doit retourner une liste vide si aucun résultat.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pContenu == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL}, LOG et lève une exception.</li>
	 * <li>Si {@code pContenu} est Blank, délègue 
	 * à {@link #rechercherTous()} et retourne 
	 * tous les enregistrements.</li>
	 * <li>Si {@code pContenu} n'est pas Blank,  
	 * retourne la liste des objets dont le libellé 
	 * contient {@code pContenu}
	 * (liste vide si aucun résultat).</li>
	 * <ul>
	 * <li>positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_RECHERCHE_VIDE} si la 
	 * liste de résultats est vide.</li>
	 * <li>positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_RECHERCHE_OK} si la 
	 * liste de résultats n'est pas vide.</li>
	 * </ul>
	 * </ul>
	 * <p><b>INVARIANT technique :</b> le composant Gateway ne renvoie jamais
	 * de liste contenant des éléments {@code null}.</p>
	 * </div>
	 *
	 * @param pContenu : String : 
	 * contenu partiel du libellé (recherche rapide).
	 * @return List&lt;TypeProduitDTO.OutputDTO&gt; : 
	 * Liste de tous les objets métier dont le libellé contient pContenu.
	 * @throws Exception
	 */
	List<TypeProduitDTO.OutputDTO> findByLibelleRapide(String pContenu) 
			throws Exception;



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche un TypeProduit dans 
	 * le stockage à partir d'un InputDTO.</p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, retourne {@code null} 
	 * et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_RECHERCHE_OBJ_NULL} 
	 * (aucun LOG, aucune exception).</li>
	 * <li>délègue la recherche de l'objet métier 
	 * à {@link #findByLibelle(String)}</li>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO : DTO de recherche.
	 * @return TypeProduitDTO.OutputDTO : DTO résultat ou null.
	 * @throws Exception
	 */
	TypeProduitDTO.OutputDTO findByDTO(
			TypeProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Retourne un {@link TypeProduit}
	 * sous forme de OutputDTO déterminé par son ID dans le stockage.</p>
	 * <ul>
	 * <li>retourne null si pId == null.</li>
	 * <li>retourne null si l'objet métier n'est pas trouvé.</li>
	 * <li>convertit la réponse en DTO et le retourne.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pId == null}, retourne {@code null} et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL} 
	 * (aucun LOG, aucune exception).</li>
 	 * <li>Si aucun objet n'est trouvé en stockage, positionne 
 	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} + pId
 	 * , et retourne {@code null}.</li>
 	 * <li>Sinon, positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_SUCCES_RECHERCHE} 
	 * et retourne l'OutputDTO correspondant à l'objet trouvé.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pId : Long : ID dans le stockage.
	 * @return TypeProduitDTO.OutputDTO : 
	 * l'objet métier qui possède pId, ou null.
	 * @throws Exception
	 */
	TypeProduitDTO.OutputDTO findById(Long pId) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Modifie un {@link TypeProduit}
	 * déjà existant dans le stockage.</p>
	 * <p>Cette méthode ne s'applique qu'à un objet déjà persistant.</p>
	 * <ul>
	 * <li>doit jeter une Exception si pTypeProduit == null.</li>
	 * <li>doit jeter une Exception 
	 * si le libellé est Blank.</li>
	 * <li>doit jeter une ExceptionNonPersistant si l'ID est null.</li>
	 * <li>retourne null si l'objet n'existe pas en stockage.</li>
	 * <li>retourne l'objet modifié 
	 * si une modification est effectuée.</li>
	 * <li>sinon retourne l'objet inchangé 
	 * et fournit un message utilisateur.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pTypeProduit == null}, positionne {@link #getMessage()}
	 * à {@link #MESSAGE_PARAM_NULL}, LOG et lève une exception.</li>
	 * <li>Si {@code pTypeProduit.getTypeProduit()} est Blank
	 * , positionne {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK}
	 * , LOG et lève une exception.</li>
	 * <li>Si l'objet n'existe pas dans le stockage 
	 * (recherche par libellé exact), retourne {@code null} et positionne
	 * {@link #getMessage()} à 
	 * {@link #MESSAGE_OBJ_INTROUVABLE} + libellé.</li>
	 * <li>Si l'objet existe mais n'est pas persistant (ID {@code null})
	 * , positionne {@link #getMessage()}
	 * à {@link #MESSAGE_OBJ_NON_PERSISTE} + libellé
	 * , LOG et lève une exception.</li>
	 * <li>Sinon, délègue la modification au Gateway.</li> 
	 * <li>retourne l'OutputDTO correspondant à l'objet modifié
	 * (ou {@code null} si le Gateway retourne {@code null}).</li>
	 * <ul>
	 * <li>positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_MODIF_KO} + pTypeProduit.getTypeProduit()
	 * , et retourne null si le Gateway retourne {@code null}.</li>
	 * <li>positionne {@link #getMessage()} à 
	 * {@link #MESSAGE_MODIF_OK} + pTypeProduit.getTypeProduit()
	 * , et retourne l'OutputDTO modifié en cas de succès.</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO : 
	 * Objet métier portant les modifications (hors ID).
	 * @return TypeProduitDTO.OutputDTO : 
	 * OutputDTO de même ID que pTypeProduit, modifié ou inchangé, ou null.
	 * @throws Exception
	 */
	TypeProduitDTO.OutputDTO update(
			TypeProduitDTO.InputDTO pInputDTO) throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Détruit un {@link TypeProduit}
	 * déjà existant dans le stockage.</p>
	 * <ul>
	 * <li>doit jeter une Exception 
	 * si pInputDTO == null.</li>
	 * <li>doit jeter une Exception 
	 * si pInputDTO.getTypeProduit() est blank.</li>
	 * <li>doit déléguer la suppression à la couche technique.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Si {@code pInputDTO == null}, positionne
	 *  {@link #getMessage()} à {@link #MESSAGE_PARAM_NULL}, LOG
	 *   et lève une exception (violation de contrat).</li>
	 * <li>Si {@code pInputDTO.getTypeProduit()} est Blank
	 * , positionne {@link #getMessage()} à {@link #MESSAGE_PARAM_BLANK}
	 * , LOG et lève une exception.</li>
	 * <li>Si l'objet n'existe pas en stockage 
	 * (recherche par libellé exact)
	 * , ne supprime rien
	 * , retourne, et positionne
	 * {@link #getMessage()} à {@link #MESSAGE_OBJ_INTROUVABLE} 
	 * + libellé.</li>
	 * <li>Sinon, délègue la suppression au Gateway.</li>
	 * <ul>
	 * <li>positionne {@link #getMessage()} 
	 * à {@link #MESSAGE_DELETE_OK} + libelle 
	 * si la destruction aboutit.</li>
	 * <li>alimente {@link #getMessage()} 
	 * à {@link #MESSAGE_DELETE_KO} + libelle 
	 * , LOG et jette une Exception si la destruction a échoué</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO : 
	 * l'objet métier à détruire.
	 * @throws Exception
	 */
	void delete(TypeProduitDTO.InputDTO pInputDTO) 
			throws Exception;

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Compte le nombre de {@link TypeProduit}
	 * dans le stockage.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT (métier / observable) :</p>
	 * <ul>
	 * <li>Délègue au Gateway le comptage et retourne le résultat.</li>
	 * </ul>
	 * </div>
	 *
	 * @return long : nombre d'enregistrements dans le stockage.
	 * @throws Exception
	 */
	long count() throws Exception;
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Getter du message à l'attention de l'Utilisateur 
	 * généré en cas de problème lors des opérations de stockage.</p>
	 * <p>ATTENTION : ne jamais finaliser cette méthode sinon SPRING 
	 * échoue à fournir le message dans le Proxy de TypeProduitCuService 
	 * injecté dans le CONTROLLER.</p>
	 * </div>
	 *
	 * @return String : 
	 * message à l'attention de l'Utilisateur (peut être null).
	 */
	String getMessage();

}
