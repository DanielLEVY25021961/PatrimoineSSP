/* ********************************************************************* */
/* ******************** ADAPTER SERVICE CU ***************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOProduit;
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

		/* erreur utilisateur bénigne. */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_CREER_NULL);
			return null;
		}

		/* libellé produit obligatoire. */
		if (StringUtils.isBlank(pInputDTO.getProduit())) {
			return this.traiterErreur(
					MESSAGE_CREER_NOM_BLANK,
					new ExceptionParametreBlank(MESSAGE_CREER_NOM_BLANK));
		}

		/* parent obligatoire : SousTypeProduit libellé non blank. */
		if (StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/* doublon ? */
		final Produit existant 
			= this.gateway.findByLibelle(pInputDTO.getProduit()).get(0);
		if (existant != null) {
			final String messDub = MESSAGE_DOUBLON + pInputDTO.getProduit();
			return this.traiterErreur(
					messDub, new ExceptionDoublon(messDub));
		}

		/* ============= PARENT PERSISTÉ (SousTypeProduit) ============== */
		final SousTypeProduit parentPersistant
			= this.sousTypeProduitGateway
				.findByLibelle(pInputDTO.getSousTypeProduit()).get(0);

		if (parentPersistant == null 
				|| parentPersistant.getIdSousTypeProduit() == null) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		final Produit aCreer 
			= new Produit(pInputDTO.getProduit(), parentPersistant);

		final Produit cree = this.gateway.creer(aCreer);

		if (cree == null) {
			/* même logique que les autres CU : 
			 * on garde un message KO cohérent. */
			this.message.set(KO_TECHNIQUE_RECHERCHE);
			return null;
		}

		this.message.set(MESSAGE_CREER_OK);
		return ConvertisseurMetierToOutputDTOProduit.convert(cree);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OutputDTO> rechercherTous() throws Exception {

		final List<Produit> reponses = this.gateway.rechercherTous();

		if (reponses == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		if (reponses.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return new ArrayList<OutputDTO>();
		}

		this.message.set(MESSAGE_RECHERCHE_OK);

		final List<Produit> recordsNonNullTries 
			= this.filtrerEtTrier(reponses);

		return ConvertisseurMetierToOutputDTOProduit
				.convertList(recordsNonNullTries);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> rechercherTousString() throws Exception {

		final List<OutputDTO> dtos = this.rechercherTous();

		if (dtos == null || dtos.isEmpty()) {
			/* message déjà positionné par rechercherTous(). */
			return new ArrayList<String>();
		}

		final Set<String> uniques = new LinkedHashSet<String>();
		for (final OutputDTO dto : dtos) {
			if (dto != null && StringUtils.isNotBlank(dto.getProduit())) {
				uniques.add(dto.getProduit());
			}
		}

		if (uniques.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return new ArrayList<String>();
		}

		this.message.set(MESSAGE_RECHERCHE_OK);

		return new ArrayList<String>(uniques);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultatPage<OutputDTO> rechercherTousParPage(
			final RequetePage pRequetePage) throws Exception {

		if (pRequetePage == null) {
			return this.traiterErreur(
					MESSAGE_PAGEABLE_NULL,
					new IllegalStateException(MESSAGE_PAGEABLE_NULL));
		}

		final ResultatPage<Produit> resultatPagine
			= this.gateway.rechercherTousParPage(pRequetePage);

		if (resultatPagine == null) {
			this.message.set(MESSAGE_RECHERCHE_PAGINEE_KO);
			return null;
		}

		final List<Produit> contenus = resultatPagine.getContent();

		final List<Produit> recordsNonNullTries = this.filtrerEtTrier(contenus);

		final List<OutputDTO> dtos 
			= ConvertisseurMetierToOutputDTOProduit
					.convertList(recordsNonNullTries);
		

		final int numeroPage = pRequetePage.getPageNumber();
		final int pgSize = pRequetePage.getPageSize();
		final long totalElements = this.safeTotalElements(resultatPagine);

		final ResultatPage<OutputDTO> rp = new ResultatPage<OutputDTO>(
				dtos, numeroPage, pgSize, totalElements);

		this.message.set(MESSAGE_RECHERCHE_PAGINEE_OK);

		return rp;
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO findByLibelle(
			final String pLibelle) throws Exception {

		if (StringUtils.isBlank(pLibelle)) {
			this.message.set(MESSAGE_PARAM_BLANK);
			return null;
		}

		final Produit reponse = this.gateway.findByLibelle(pLibelle).get(0);

		if (reponse == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pLibelle);
			return null;
		}

		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		return ConvertisseurMetierToOutputDTOProduit.convert(reponse);
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
