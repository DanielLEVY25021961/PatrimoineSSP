/* ********************************************************************* */
/* ******************** ADAPTER SERVICE CU ***************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOProduit;
import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.ProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitCuService :</p>
 * <p>SERVICE ADAPTER METIER Use Case implémentant le PORT {@link ProduitICuService}.</p>
 * <p>Délègue la persistance au Service Gateway {@link ProduitGatewayIService}.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
@Service(value = "ProduitCuService")
@Profile({ "desktop", "dev", "prod" })
public class ProduitCuService implements ProduitICuService {

	// *************************** ATTRIBUTS ******************************/

	/**
	 * Service Gateway (Technique) pour Produit.
	 */
	private final ProduitGatewayIService gateway;

	/**
	 * Service Gateway (Technique) pour SousTypeProduit (parent).
	 */
	private final SousTypeProduitGatewayIService sousTypeProduitGateway;

	/**
	 * Message observable (ThreadLocal).
	 */
	private final ThreadLocal<String> message = new ThreadLocal<String>();
	
	// *************************** LOG ******************************/

	/**
	 * Log Apache Commons.
	 */
	private static final Log LOG = LogFactory.getLog(ProduitCuService.class);


	// ************************* CONSTRUCTEURS ****************************/

	/**
	 * <div>
	 * <p>Constructeur Spring (DI).</p>
	 * </div>
	 *
	 * @param pGateway gateway Produit
	 * @param pSousTypeProduitGateway gateway SousTypeProduit
	 */
	public ProduitCuService(
			final ProduitGatewayIService pGateway,
			final SousTypeProduitGatewayIService pSousTypeProduitGateway) {
		super();
		this.gateway = pGateway;
		this.sousTypeProduitGateway = pSousTypeProduitGateway;
	}

	// *************************** METHODES *******************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO creer(
			final InputDTO pInputDTO) throws Exception {

		/*
		 * Erreur utilisateur bénigne :
		 * aucun traitement, aucun LOG, aucune Exception.
		 * Si pInputDTO == null :
		 * Retourne null avec un message utilisateur MESSAGE_CREER_NULL.
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_CREER_NULL);
			return null;
		}

		/*
		 * Récupère le libellé métier du Produit.
		 */
		final String libelle = pInputDTO.getProduit();

		/*
		 * Si le libellé est blank :
		 * émet MESSAGE_CREER_NOM_BLANK + LOG + ExceptionParametreBlank.
		 */
		if (StringUtils.isBlank(libelle)) {
			return this.traiterErreur(
					MESSAGE_CREER_NOM_BLANK,
					new ExceptionParametreBlank(MESSAGE_CREER_NOM_BLANK));
		}

		/*
		 * Le parent SousTypeProduit est une 
		 * précondition observable du SERVICE UC.
		 * Si son libellé est blank :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		final String libelleParent = pInputDTO.getSousTypeProduit();

		if (StringUtils.isBlank(libelleParent)) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Vérifie le doublon fonctionnel.
		 * Toute anomalie technique pendant ce contrôle
		 * est rationalisée côté UC.
		 */
		final boolean doublon;

		try {

			doublon = this.isDoublon(pInputDTO);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
							+ messageSecurise,
					e);
		}

		/*
		 * Si doublon :
		 * émet MESSAGE_DOUBLON + libellé + LOG + ExceptionDoublon.
		 */
		if (doublon) {

			final String messageDoublon = MESSAGE_DOUBLON + libelle;

			return this.traiterErreur(
					messageDoublon,
					new ExceptionDoublon(messageDoublon));
		}

		/*
		 * Récupère le parent persistant.
		 * Toute anomalie technique de recherche du parent
		 * est rationalisée côté UC.
		 */
		final SousTypeProduit parentPersistant;

		try {

			parentPersistant = this.rechercherParentPersistant(pInputDTO);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
							+ messageSecurise,
					e);
		}

		/*
		 * Si le parent n'existe pas en stockage
		 * ou n'est pas persistant :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdSousTypeProduit() == null) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Convertit l'InputDTO en objet métier
		 * puis rattache explicitement le parent persistant.
		 */
		final Produit produit = this.convertirInputDTOEnMetier(pInputDTO);

		produit.setSousTypeProduit(parentPersistant);

		/*
		 * Délègue la création au GATEWAY.
		 * Toute anomalie technique de création
		 * est rationalisée côté UC.
		 */
		final Produit cree;

		try {

			/* Délègue la création au GATEWAY. */
			cree = this.gateway.creer(produit);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
							+ messageSecurise,
					e);
		}

		/*
		 * Le GATEWAY ne doit pas conduire à un succès
		 * si aucun objet créé n'est réellement disponible.
		 * Si cree == null :
		 * émet MESSAGE_CREATION_TECHNIQUE_KO_CREER
		 * + LOG + IllegalStateException.
		 */
		if (cree == null) {
			return this.traiterErreur(
					MESSAGE_CREATION_TECHNIQUE_KO_CREER,
					new IllegalStateException(
							MESSAGE_CREATION_TECHNIQUE_KO_CREER));
		}

		/*
		 * Prépare la réponse utilisateur finale.
		 * Le message de succès n'est positionné
		 * qu'après conversion réussie.
		 */
		final ProduitDTO.OutputDTO dto;

		try {

			/* Convertit l'objet métier créé en OutputDTO. */
			dto = ConvertisseurMetierToOutputDTOProduit.convert(cree);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
							+ messageSecurise,
					e);
		}

		/*
		 * Si dto == null :
		 * émet MESSAGE_CONVERSION_TECHNIQUE_KO_CREER
		 * + LOG + IllegalStateException.
		 */
		if (dto == null) {
			return this.traiterErreur(
					MESSAGE_CONVERSION_TECHNIQUE_KO_CREER,
					new IllegalStateException(
							MESSAGE_CONVERSION_TECHNIQUE_KO_CREER));
		}

		/*
		 * Positionne le message observable seulement
		 * après préparation complète de la réponse.
		 */
		this.message.set(MESSAGE_CREER_OK);

		/*
		 * Retourne l'OutputDTO final.
		 */
		return dto;
	}

	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<OutputDTO> rechercherTous() throws Exception {

		/*
		 * Appelle le GATEWAY pour lire tous les Produit.
		 */
		final List<Produit> records;

		try {

			records = this.gateway.rechercherTous();

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Retire les null et trie les objets métier.
		 */
		final List<Produit> recordsNonNullTries
			= this.filtrerEtTrier(records);

		/*
		 * Convertit la liste métier en OutputDTO
		 * puis dédoublonne la réponse.
		 */
		final List<OutputDTO> dtos;

		try {

			dtos = ConvertisseurMetierToOutputDTOProduit
					.convertList(recordsNonNullTries);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					e);
		}

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/*
		 * Retourne toujours une liste d'OutputDTO non null
		 * (éventuellement vide).
		 */
		return dtos;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> rechercherTousString() throws Exception {

		/*
		 * Délègue d'abord la recherche exhaustive
		 * à rechercherTous().
		 */
		final List<OutputDTO> dtos = this.rechercherTous();

		/*
		 * Extrait ensuite uniquement les libellés Produit exploitables.
		 */
		final List<String> retour = new ArrayList<>();

		for (final OutputDTO dto : dtos) {
			if (dto != null && StringUtils.isNotBlank(dto.getProduit())) {
				retour.add(dto.getProduit());
			}
		}

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (retour.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/* Retourne la liste exhaustive des libellés. */
		return retour;
	}
	
	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public ResultatPage<OutputDTO> rechercherTousParPage(
			final RequetePage pRequetePage) throws Exception {

		/*
		 * Le contrat UC refuse une requête de pagination null.
		 * Si pRequetePage == null : émet MESSAGE_PAGEABLE_NULL
		 * + LOG + IllegalStateException.
		 */
		if (pRequetePage == null) {
			return this.traiterErreur(
					MESSAGE_PAGEABLE_NULL,
					new IllegalStateException(MESSAGE_PAGEABLE_NULL));
		}

		/*
		 * Délègue au GATEWAY la recherche paginée.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final ResultatPage<Produit> resultatPagine;

		try {

			/* Délègue au GATEWAY la recherche paginée. */
			resultatPagine = this.gateway.rechercherTousParPage(pRequetePage);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					e);
		}

		/*
		 * Sécurise le contrat observable du UC :
		 * une réponse paginée null du GATEWAY
		 * est une rupture technique.
		 * Si resultatPagine == null : émet MESSAGE_RECHERCHE_PAGINEE_KO
		 * + LOG + IllegalStateException.
		 */
		if (resultatPagine == null) {
			return this.traiterErreur(
					MESSAGE_RECHERCHE_PAGINEE_KO,
					new IllegalStateException(MESSAGE_RECHERCHE_PAGINEE_KO));
		}

		/*
		 * Prépare la réponse paginée utilisateur complète :
		 * retrait des nulls, tri métier,
		 * conversion en OutputDTO avec dédoublonnage,
		 * puis reconstruction d'un ResultatPage cohérent.
		 */
		final ResultatPage<OutputDTO> resultatUc;

		try {

			/* Récupère la liste d'objets métier auprès du resultatPagine. */
			final List<Produit> contenus = resultatPagine.getContent();

			/* Retire les null, trie la liste d'objets métier. */
			final List<Produit> recordsNonNullTries
					= this.filtrerEtTrier(contenus);

			/* Convertit la liste d'objets métier sans null
			 * en dédoublonnant et en conservant l'ordre. */
			final List<ProduitDTO.OutputDTO> dtos
					= ConvertisseurMetierToOutputDTOProduit.convertList(
							recordsNonNullTries);

			final int numeroPage = resultatPagine.getPageNumber();
			final int pageSize = resultatPagine.getPageSize();
			final long totalElements = this.safeTotalElements(resultatPagine);

			/* Reconstruit un ResultatPage cohérent. */
			resultatUc = new ResultatPage<OutputDTO>(
					dtos,
					numeroPage,
					pageSize,
					totalElements);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					e);
		}

		/*
		 * Le message observable de succès MESSAGE_RECHERCHE_PAGINEE_OK
		 * n'est positionné qu'après préparation complète
		 * de la réponse paginée utilisateur.
		 */
		this.message.set(MESSAGE_RECHERCHE_PAGINEE_OK);

		/*
		 * Retourne toujours un ResultatPage non null
		 * lorsque le scénario se termine avec succès.
		 */
		return resultatUc;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProduitDTO.OutputDTO> findByLibelle(
			final String pLibelle) throws Exception {

		/*
		 * Erreur utilisateur bénigne :
		 * libellé blank => null + MESSAGE_PARAM_BLANK, sans exception.
		 */
		if (StringUtils.isBlank(pLibelle)) {
			this.message.set(MESSAGE_PARAM_BLANK);
			return null;
		}

		/*
		 * Délègue au GATEWAY la recherche exacte par libellé.
		 */
		final List<Produit> reponses = this.gateway.findByLibelle(pLibelle);

		/*
		 * Une réponse technique null du GATEWAY
		 * est une anomalie de recherche.
		 * Si reponses == null : 
		 * émet un message KO_TECHNIQUE_RECHERCHE + LOG + RuntimeException.
		 */
		if (reponses == null) {
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					new RuntimeException(KO_TECHNIQUE_RECHERCHE));
		}

		/*
		 * Retire les null, trie les objets métier,
		 * puis convertit la réponse en OutputDTO.
		 */
		final List<Produit> recordsNonNullTries
				= this.filtrerEtTrier(reponses);

		final List<ProduitDTO.OutputDTO> dtos
				= ConvertisseurMetierToOutputDTOProduit
						.convertList(recordsNonNullTries);

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		return dtos;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OutputDTO> findByLibelleRapide(
			final String pContenu) throws Exception {

		/* CONTRAT : null => exception ; 
		 * blank => délègue rechercherTous(). */
		if (pContenu == null) {
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new IllegalStateException(MESSAGE_PARAM_NULL));
		}

		if (StringUtils.isBlank(pContenu)) {
			return this.rechercherTous();
		}

		final List<Produit> reponses 
			= this.gateway.findByLibelleRapide(pContenu);

		if (reponses == null) {
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					new RuntimeException(KO_TECHNIQUE_RECHERCHE));
		}

		if (reponses.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		final List<Produit> recordsNonNullTries 
			= this.filtrerEtTrier(reponses);

		return ConvertisseurMetierToOutputDTOProduit
						.convertList(recordsNonNullTries);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO findByDTO(
			final InputDTO pInputDTO) throws Exception {

		/* CONTRAT : DTO null => null + message, aucune exception. */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_RECHERCHE_OBJ_NULL);
			return null;
		}

		/* CONTRAT : parent blank => message + exception 
		 * (avant toute technique). */
		if (StringUtils.isBlank(pInputDTO.getTypeProduit()) 
				|| StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/* récupère le parent persistant (avec ID). */
		final SousTypeProduit parentPersistant
			= this.sousTypeProduitGateway
				.findByLibelle(pInputDTO.getSousTypeProduit()).get(0);

		/* si parent absent, aucun résultat possible. */
		if (parentPersistant == null 
				|| parentPersistant.getIdSousTypeProduit() == null) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;
		}

		try {

			final List<Produit> possibles 
				= this.gateway.findAllByParent(parentPersistant);

			if (possibles == null || possibles.isEmpty()) {
				this.message.set(MESSAGE_RECHERCHE_VIDE);
				return null;
			}

			/* si libellé produit blank -> on ne peut pas matcher :
			 *  recherche vide. */
			if (StringUtils.isBlank(pInputDTO.getProduit())) {
				this.message.set(MESSAGE_RECHERCHE_VIDE);
				return null;
			}

			for (final Produit p : possibles) {
				if (p != null && Strings.CI.equals(
						p.getProduit(), pInputDTO.getProduit())) {
					this.message.set(MESSAGE_SUCCES_RECHERCHE);
					return ConvertisseurMetierToOutputDTOProduit
							.convert(p);
				}
			}

			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;

		} catch (Exception e) {

			/* KO technique. */
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					e);
		}
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OutputDTO> findAllByParent(
			final SousTypeProduitDTO.InputDTO pSousTypeProduit) 
						throws Exception {

		if (pSousTypeProduit == null) {
			return this.traiterErreur(
					RECHERCHE_SOUSTYPEPRODUIT_NULL,
					new RuntimeException(RECHERCHE_SOUSTYPEPRODUIT_NULL));
		}

		if (StringUtils.isBlank(pSousTypeProduit.getSousTypeProduit())) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		final SousTypeProduit parentPersistant
			= this.sousTypeProduitGateway
				.findByLibelle(pSousTypeProduit.getSousTypeProduit()).get(0);

		if (parentPersistant == null 
				|| parentPersistant.getIdSousTypeProduit() == null) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		final List<Produit> reponses 
			= this.gateway.findAllByParent(parentPersistant);

		if (reponses == null) {
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					new RuntimeException(KO_TECHNIQUE_RECHERCHE));
		}

		if (reponses.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		final List<Produit> recordsNonNullTries 
			= this.filtrerEtTrier(reponses);

		return ConvertisseurMetierToOutputDTOProduit
					.convertList(recordsNonNullTries);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO findById(
			final Long pId) throws Exception {

		if (pId == null) {
			this.message.set(MESSAGE_PARAM_NULL);
			return null;
		}

		final Produit reponse = this.gateway.findById(pId);

		if (reponse == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pId);
			return null;
		}

		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		return ConvertisseurMetierToOutputDTOProduit.convert(reponse);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO update(
			final InputDTO pInputDTO) throws Exception {

		if (pInputDTO == null) {
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
		}

		if (StringUtils.isBlank(pInputDTO.getProduit())) {
			return this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
		}

		final Produit existant 
			= this.gateway.findByLibelle(pInputDTO.getProduit()).get(0);

		if (existant == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE 
					+ pInputDTO.getProduit());
			return null;
		}

		if (existant.getIdProduit() == null) {
			return this.traiterErreur(
					MESSAGE_OBJ_NON_PERSISTE + pInputDTO.getProduit(),
					new ExceptionNonPersistant(
							MESSAGE_OBJ_NON_PERSISTE 
								+ pInputDTO.getProduit()));
		}

		/* conserve l'ID, et remplace le libellé (ex : upper) 
		 * selon la logique projet. */
		final String nouveauLibelle 
			= pInputDTO.getProduit().toUpperCase(Locale.getDefault());

		final Produit aModifier 
			= new Produit(nouveauLibelle, existant.getSousTypeProduit());
		aModifier.setIdProduit(existant.getIdProduit());

		final Produit modifie = this.gateway.update(aModifier);

		if (modifie == null) {
			this.message.set(MESSAGE_MODIF_KO + pInputDTO.getProduit());
			return null;
		}

		this.message.set(MESSAGE_MODIF_OK 
				+ this.safeParentLibelle(modifie));

		return ConvertisseurMetierToOutputDTOProduit.convert(modifie);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(
			final InputDTO pInputDTO) throws Exception {

		if (pInputDTO == null) {
			this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
			return;
		}

		if (StringUtils.isBlank(pInputDTO.getProduit())) {
			this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
			return;
		}

		final Produit existant 
			= this.gateway.findByLibelle(pInputDTO.getProduit()).get(0);

		if (existant == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE 
					+ pInputDTO.getProduit());
			return;
		}

		try {
			this.gateway.delete(existant);
			this.message.set(MESSAGE_DELETE_OK + pInputDTO.getProduit());
		} catch (Exception e) {
			this.traiterErreur(
					MESSAGE_DELETE_KO 
					+ pInputDTO.getProduit(),
					new RuntimeException(
							MESSAGE_DELETE_KO + pInputDTO.getProduit(), e));
		}
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long count() throws Exception {
		return this.gateway.count();
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return this.message.get();
	}

	
	
	// *************************** METHODES UTILITAIRES ********************/


	/**
	 * <div>
	 * <p>Détermine si un {@link ProduitDTO.InputDTO}
	 * correspond à un doublon fonctionnel.</p>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO
	 * @return boolean : true si doublon, false sinon.
	 * @throws Exception
	 */
	private boolean isDoublon(
			final ProduitDTO.InputDTO pInputDTO) throws Exception {

		if (pInputDTO == null) {
			return false;
		}

		final String libelleProduitRecherche = pInputDTO.getProduit();
		final String libelleSousTypeRecherche = pInputDTO.getSousTypeProduit();
		final String libelleTypeRecherche = pInputDTO.getTypeProduit();

		if (StringUtils.isBlank(libelleSousTypeRecherche)) {
			return false;
		}

		final List<Produit> produitsExistants
				= this.gateway.findByLibelle(libelleProduitRecherche);

		if (produitsExistants == null || produitsExistants.isEmpty()) {
			return false;
		}

		for (final Produit existant : produitsExistants) {

			if (existant == null) {
				continue;
			}

			final String produitExistant = existant.getProduit();
			final SousTypeProduitI sousTypeExistantObjet = existant.getSousTypeProduit();
			final String sousTypeExistant = sousTypeExistantObjet != null
					? sousTypeExistantObjet.getSousTypeProduit()
					: null;
			final String typeExistant = sousTypeExistantObjet != null
					&& sousTypeExistantObjet.getTypeProduit() != null
							? sousTypeExistantObjet.getTypeProduit().getTypeProduit()
							: null;

			final boolean typeCompatible = StringUtils.isBlank(libelleTypeRecherche)
					|| Strings.CI.equals(libelleTypeRecherche, typeExistant);

			if (Strings.CI.equals(libelleProduitRecherche, produitExistant)
					&& Strings.CI.equals(libelleSousTypeRecherche, sousTypeExistant)
					&& typeCompatible) {
				return true;
			}
		}

		return false;
	}

	
	
	/**
	 * <div>
	 * <p>Recherche le {@link SousTypeProduit} parent persistant
	 * correspondant au DTO d'entrée.</p>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO
	 * @return SousTypeProduit : parent persistant ou null.
	 * @throws Exception
	 */
	private SousTypeProduit rechercherParentPersistant(
			final ProduitDTO.InputDTO pInputDTO) throws Exception {

		if (pInputDTO == null
				|| StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			return null;
		}

		final String libelleSousTypeRecherche = pInputDTO.getSousTypeProduit();
		final String libelleTypeRecherche = pInputDTO.getTypeProduit();

		final List<SousTypeProduit> parents
				= this.sousTypeProduitGateway.findByLibelle(libelleSousTypeRecherche);

		if (parents == null || parents.isEmpty()) {
			return null;
		}

		for (final SousTypeProduit parent : parents) {

			if (parent == null) {
				continue;
			}

			if (parent.getIdSousTypeProduit() == null) {
				continue;
			}

			final String libelleSousType = parent.getSousTypeProduit();
			final String libelleType = parent.getTypeProduit() != null
					? parent.getTypeProduit().getTypeProduit()
					: null;

			final boolean typeCompatible = StringUtils.isBlank(libelleTypeRecherche)
					|| Strings.CI.equals(libelleTypeRecherche, libelleType);

			if (Strings.CI.equals(libelleSousTypeRecherche, libelleSousType)
					&& typeCompatible) {
				return parent;
			}
		}

		return null;
	}

	
	
	/**
	 * <div>
	 * <p>Convertit un InputDTO en objet métier {@link Produit}.</p>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO
	 * @return Produit : objet métier (ou null si pInputDTO == null)
	 */
	private Produit convertirInputDTOEnMetier(
			final ProduitDTO.InputDTO pInputDTO) {

		if (pInputDTO == null) {
			return null;
		}

		return new Produit(pInputDTO.getProduit());
	}

	
	
	/**
	 * <div>
	 * <p>Traite une erreur : 
	 * log, positionne le message thread-local, puis jette l'exception.</p>
	 * </div>
	 *
	 * @param pMessage message à positionner
	 * @param pException exception à jeter
	 * @param <T> type de retour (jamais atteint)
	 * @return T (jamais atteint)
	 * @throws Exception
	 */
	private <T> T traiterErreur(
			final String pMessage,
			final Exception pException) throws Exception {

		this.message.set(pMessage);

		if (LOG.isErrorEnabled()) {
			LOG.error(pMessage, pException);
		}

		throw pException;
	}

	
	
	/**
	 * <div>
	 * <p>Filtre les nulls, puis trie 
	 * par libellé produit (case-insensitive).</p>
	 * </div>
	 *
	 * @param pListe liste potentiellement null
	 * @return liste non nulle filtrée/triée
	 */
	private List<Produit> filtrerEtTrier(
			final List<Produit> pListe) {

		final List<Produit> resultat = new ArrayList<Produit>();

		if (pListe == null) {
			return resultat;
		}

		for (final Produit p : pListe) {
			if (p != null) {
				resultat.add(p);
			}
		}

		resultat.sort(new Comparator<Produit>() {
			@Override
			public int compare(final Produit o1, final Produit o2) {
				final String s1 = (o1 != null) ? o1.getProduit() : null;
				final String s2 = (o2 != null) ? o2.getProduit() : null;
				return Strings.CI.compare(s1, s2);
			}
		});

		return resultat;
	}

	
	
	/**
	 * <div>
	 * <p>Récupère un totalElements sûr depuis un ResultatPage.</p>
	 * </div>
	 *
	 * @param rp resultat page
	 * @return totalElements >= 0
	 */
	private long safeTotalElements(
			final ResultatPage<?> rp) {

		if (rp == null) {
			return 0L;
		}

		final long total = rp.getTotalElements();

		if (total < 0L) {
			return 0L;
		}

		return total;
	}

	
	
	/**
	 * <div>
	 * <p>Extrait un libellé parent (SousTypeProduit) 
	 * pour les messages de modification.</p>
	 * </div>
	 *
	 * @param pProduit produit
	 * @return libellé parent ou "?"
	 */
	private String safeParentLibelle(
			final Produit pProduit) {

		if (pProduit == null || pProduit.getSousTypeProduit() == null) {
			return "?";
		}

		final SousTypeProduitI stp = pProduit.getSousTypeProduit();

		if (StringUtils.isBlank(stp.getSousTypeProduit())) {
			return "?";
		}

		return stp.getSousTypeProduit();
	}

	
	
}
