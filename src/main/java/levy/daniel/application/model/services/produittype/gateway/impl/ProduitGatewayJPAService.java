/* ********************************************************************* */
/* *********************** ADAPTER GATEWAY ***************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl; // NOPMD by danyl on 03/02/2026 00:53

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduitI;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParentNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGatewayNonPersistent;
import levy.daniel.application.model.services.produittype.gateway.ProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.ProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.SousTypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ConvertisseurJPAToMetier;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ConvertisseurMetierToJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitGatewayJPAService.java :
 * </p>
 * <p style="font-weight:bold;">ADAPTER SERVICE GATEWAY</p>
 * 
 *  <p>
 * Cette classe modélise :
 * un <span style="font-weight:bold;">SERVICE</span> chargé de :
 * </p>
 * <ul>
 * <li>transformer un <span style="font-weight:bold;">
 * objet métier</span>
 * {@link Produit} 
 * en <span style="font-weight:bold;">Entity JPA</span>
 *  {@link ProduitJPA}.</li>
 * <li>Manipuler (Sauvegarder, Modifier, ...) l'
 * <span style="font-weight:bold;">Entity JPA</span>
 * au moyen d'un <span style="font-weight:bold;">
 * Repository (DAO) JPA</span>.</li>
 * </ul>
 *
 * <p>
 * ADAPTER GATEWAY JPA implémentant le PORT GATEWAY 
 * {@link ProduitGatewayIService}.
 * </p>
 *
 * <p>
 * Stratégie : en cas de problème dans la couche stockage,
 * jeter une Exception circonstanciée conforme 
 * aux constantes contractuelles décrites dans le PORT GATEWAY.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 2.0
 * @since 25 janvier 2026
 */
@Service("ProduitGatewayJPAService")
@Transactional
public class ProduitGatewayJPAService 
	implements ProduitGatewayIService {

	// *************************** CONSTANTES ****************************/

	/**
	 * <div>
	 * <p>"idProduit"</p>
	 * </div>
	 */
	private static final String CHAMP_ID_PRODUIT = "idProduit";

	/**
	 * <div>
	 * <p>"produit"</p>
	 * </div>
	 */
	private static final String CHAMP_LIBELLE_PRODUIT = "produit";
	
	// ************************ATTRIBUTS**********************************/
	
	/**
	 * <div>
	 * <p>Repository (DAO) pour {@link Produit} (enfant).</p>
	 * <p>Injecté via le constructeur par Spring.</p>
	 * </div>
	 */
	private final ProduitDaoJPA produitDaoJPA;

	/**
	* <div>
	* <p>Repository (DAO) pour {@link SousTypeProduit} (parent)</p>
	* <p>Utilisé notamment pour vérifier la persistance du parent.</p>
	* <p>Injecté via le constructeur par Spring.</p>
	* </div>
	*/
	private final SousTypeProduitDaoJPA sousTypeProduitDaoJPA;
	
	/**
	 * <div>
	 * <p>EntityManager pour la gestion du cache Hibernate.</p>
	 * <p>Permet de vider explicitement le cache de premier niveau
	 * après des opérations critiques comme la suppression.</p>
	 * </div>
	 */
	@PersistenceContext
	private EntityManager entityManager;
	
	
	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger : </p>
	 * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances : </p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG 
		= LogManager.getLogger(ProduitGatewayJPAService.class);


	// *************************** CONSTRUCTEURS *************************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * CONSTRUCTEUR COMPLET appelé automatiquement par SPRING
	 * pour injecter : </p>
	 * <ul>
	 * <li>un DAO ProduitDaoJPA pour la 
	 * persistance de l'enfant {@link Produit}.</li>
	 * <li>un DAO SousTypeProduitDaoJPA pour la 
	 * persistance du parent {@link SousTypeProduit}.</li>
	 * </ul>
	 * <p>ATTENTION : Ne surtout pas créer de Constructeur d'arité nulle
	 * dans cette classe, faute de quoi SPRING ne pourra plus injecter.</p>
	 * </div>
	 *
	 * @param pProduitDaoJPA : ProduitDaoJPA
	 * @param pSousTypeProduitDaoJPA : SousTypeProduitDaoJPA
	 */
	public ProduitGatewayJPAService(
			@Qualifier("ProduitDaoJPA")
			final ProduitDaoJPA pProduitDaoJPA,
			@Qualifier("SousTypeProduitDaoJPA")
			final SousTypeProduitDaoJPA pSousTypeProduitDaoJPA) {
		super();
		this.produitDaoJPA = pProduitDaoJPA;
		this.sousTypeProduitDaoJPA = pSousTypeProduitDaoJPA;
	}



	// ***************************** METHODES ****************************/

	/**
	* {@inheritDoc}
	*/
	@Override
	public Produit creer(final Produit pObject) 
			throws Exception {

	    /* Si pObject == null : 
	     * jette une ExceptionAppliParamNull
	     * avec un message résolu par
	     * resoudreMessageParamNull(String). */
	    if (pObject == null) {
	        throw new ExceptionAppliParamNull(
	                MESSAGE_CREER_KO_PARAM_NULL);
	    }

	    /* Si le libellé de pObject est blank :
	     * jette une ExceptionAppliLibelleBlank
	     * avec un message résolu par
	     * resoudreMessageLibelleBlank(String). */
	    if (isBlank(pObject.getProduit())) {
	        throw new ExceptionAppliLibelleBlank(
	                MESSAGE_CREER_KO_LIBELLE_BLANK);
	    }

	    /* Récupère le parent de l'objet métier. */
	    final SousTypeProduitI parent = pObject.getSousTypeProduit();

	    /* 
		  * récupère le parent persisté si il existe.
		  * ATTENTION : cette méthode peut jeter des Exceptions.
		  * Vérifie la persistance du parent (contrat PORT) :
	      * - parent null → ExceptionAppliParentNull
	      * - libellé parent blank → ExceptionAppliLibelleBlank
	      * - parent non persistant → ExceptionTechniqueGatewayNonPersistent
	      * */
	    final SousTypeProduitJPA parentPersistant =
	            verifierPersistanceParent(parent, PREFIX_MESSAGE_CREER);

	    try {
	    	
	        /* Convertit l'objet métier en Entity JPA. */
	        final ProduitJPA entity 
	        	= ConvertisseurMetierToJPA
	        		.produitMETIERToJPA(pObject);

	        /* injecte le parent persistant
			 * dans l'Entity à persister. */
	        entity.setSousTypeProduit(parentPersistant);

	        /* délègue au DAO la création dans le stockage. */
			/* récupère l'Entity JPA créée dans le stockage. */
	        final ProduitJPA sauvegarde 
	        	= this.produitDaoJPA.save(entity);

	        /* Si le DAO répond null :
	         * jette une ExceptionTechniqueGateway
	         * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
	        if (sauvegarde == null) {
	            throw new ExceptionTechniqueGateway(
	                    ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /* convertit l'Entity JPA créée en objet métier. */
	        final Produit reponse 
	        	= ConvertisseurJPAToMetier
	        		.produitJPAToMetier(sauvegarde);
	        
	        /* retourne l'objet métier stocké dans le stockage. */
			return reponse;

	    } catch (final ExceptionTechniqueGateway e) {
	    	
	        /* Préserve le message contractuel
	         * (messages techniques déjà construits). */
	        throw e;
	        
	    } catch (final Exception e) {

			final String message 
				= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);

			/* jette une ExceptionTechniqueGateway avec un
			 * message ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e)
			 * et propage l'Exception technique cause. */
			throw new ExceptionTechniqueGateway(message, e);

	    }
	}
	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<Produit> rechercherTous() throws Exception {
	    try {
	    	
	        /* Récupère la liste complète des Entities auprès du DAO. */
	        final List<ProduitJPA> entities 
	        	= this.produitDaoJPA.findAll();

	        /* 
	         * Si this.produitDaoJPA.findAll() retourne null :
	         * jette une ExceptionTechniqueGateway
	         * avec un message ERREUR_TECHNIQUE_KO_STOCKAGE. 
	         */
	        if (entities == null) {
	            throw new ExceptionTechniqueGateway(
	                    ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /*
	         * retourne une liste vide si
	         * this.produitDaoJPA.findAll()
	         * retourne une liste vide.
	         */
	        if (entities.isEmpty()) {
	            return new ArrayList<Produit>();
	        }

	        /* Applique le traitement standard des listes :
	         * 1. Filtrage des null
	         * 2. Tri par parent puis libellé
	         * (via construireComparateurProduit)
	         * 3. Dédoublonnage tout en conservant l'ordre (LinkedHashSet)
	         * 4. Conversion en objets métier. */
	        final List<Produit> reponse
	        	= this.filtrerTrierDedoublonner(entities);

	        /* retourne la liste d'objets métier. */
	        return reponse;

	    } catch (final ExceptionTechniqueGateway e) {
	    	
	        /* Préserve le message contractuel
	         * (messages techniques déjà construits). */
	        throw e;
	        
	    } catch (final Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* jette une ExceptionTechniqueGateway 
	         * avec un message ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e)
	         * et propage l'Exception technique cause. */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}
	
	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public ResultatPage<Produit> rechercherTousParPage(
			final RequetePage pRequetePage) throws Exception {
		
	    /* Choisit la requêtePage par défaut si pRequetePage == null. */
	    final RequetePage requete = (pRequetePage != null) 
	    		? pRequetePage : new RequetePage();

	    try {
	    	
	        /* convertit une RequetePage neutre en Pageable Spring. */
	        final Pageable pageable 
	        	= convertirRequetePageEnPageable(requete);

	        /* récupère la page des Entities auprès du DAO. */
	        /* Page Spring -> conversion du contenu + total. */
	        final Page<ProduitJPA> pageJPA 
	        	= this.produitDaoJPA.findAll(pageable);

	        /* si pageJPA == null : 
	         * jette une ExceptionTechniqueGateway 
	         * avec ERREUR_TECHNIQUE_KO_STOCKAGE. */
	        if (pageJPA == null) {
	            throw new ExceptionTechniqueGateway(
	            		ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        final List<ProduitJPA> contenuJPA = pageJPA.getContent();

	        /* si contenuJPA == null : 
	         * jette une ExceptionTechniqueGateway 
	         * avec ERREUR_TECHNIQUE_KO_STOCKAGE. */
	        if (contenuJPA == null) {
	            throw new ExceptionTechniqueGateway(
	            		ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /* convertit la page d'Entities en page d'objets métier. */
	        /* Optimisation : Construction directe du ResultatPage 
	         * sans copie intermédiaire.
	         * 1. Filtre les nulls
	         * 2. Convertit en objets métier
	         * 3. Construit le ResultatPage en une seule étape */
	        final List<Produit> contenu = contenuJPA.stream()
	                .filter(Objects::nonNull)
	                .map(ConvertisseurJPAToMetier::produitJPAToMetier)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toCollection(ArrayList::new));

	        /* Construit le ResultatPage avec :
			 * - contenu : liste d'objets métier (filtrée et convertie).
			 * - pageNumber : numéro de la page (0-based).
			 * - pageSize : taille de la page.
			 * - totalElements : nombre total d'éléments 
			 * (pour la pagination). */
	        final ResultatPage<Produit> resultat 
	        	= new ResultatPage<Produit>(
	                contenu,
	                pageJPA.getNumber(),
	                pageJPA.getSize(),
	                pageJPA.getTotalElements());
	        
	        /* retourne le ResultatPage. */
			return resultat;
			
	    } catch (final ExceptionTechniqueGateway e) {
	    	
	    	/* Préserve le message contractuel
			 * (messages techniques déjà construits). */
	        throw e;
	        
	    } catch (final Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* jette une ExceptionTechniqueGateway 
	         * avec ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e). */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}
	

	
	/**
	* {@inheritDoc}
	*/
	@Override
	public Produit findByObjetMetier(
			final Produit pObject) throws Exception {
		
		/*
	     * si pObject == null :
	     * jette une ExceptionAppliParamNull
	     * avec un message
	     * MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL.
	     */
	    if (pObject == null) {
	        throw new ExceptionAppliParamNull(
	        		MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
	    }

	    /*
	     * si le libellé est blank :
	     * jette une ExceptionAppliLibelleBlank
	     * avec un message
	     * MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK.
	     */
	    if (StringUtils.isBlank(pObject.getProduit())) {
	        throw new ExceptionAppliLibelleBlank(
	        		MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
	    }

	    final SousTypeProduitI parentI = pObject.getSousTypeProduit();

	    /* récupère le parent persisté si il existe. 
	     * ATTENTION : cette méthode peut jeter des Exceptions. */
	    final SousTypeProduitJPA parentPersistant 
	    	= verifierPersistanceParent(
	    			parentI, PREFIX_MESSAGE_FINDBYOBJETMETIER);

	    try {
	    	
	        /* délègue la recherche au DAO. */
	        final List<ProduitJPA> entities 
	        	= this.produitDaoJPA
	        		.findAllBySousTypeProduit(parentPersistant);

	        /* si le DAO retourne null : 
	         * jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_KO_STOCKAGE. */
	        if (entities == null) {
	            throw new ExceptionTechniqueGateway(
	            		ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /* Recherche l'Entity dont le libellé correspond 
	         * à aChercher (insensible à la casse).
	         * Retourne null si aucun résultat n'est trouvé 
	         * (conforme au contrat implicite). */
	        ProduitJPA reponseEntity = null;
	        final String aChercher = pObject.getProduit();
	        
	        for (final ProduitJPA entity : entities) {
	        	
	            if (entity == null) {
	                continue;
	            }

	            if (Strings.CI.equals(
	            		aChercher, entity.getProduit())) {
	                reponseEntity = entity;
	                break;
	            }
	        }
	        	        
	        /* retourne null si l'objet n'est pas trouvé. */
	        if (reponseEntity == null) {
	            return null;
	        }

	        /* convertit l'Entity trouvée en objet métier. */
	        final Produit reponse 
	        	= ConvertisseurJPAToMetier
	        		.produitJPAToMetier(reponseEntity);

	        /* retourne la réponse. */
	        return reponse;

	    } catch (final ExceptionTechniqueGateway e) {
	    	
	    	/* Préserve le message contractuel
			 * (messages techniques déjà construits). */
	        throw e;
	        
	    } catch (Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* jette une ExceptionTechniqueGateway 
	         * avec ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e). */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<Produit> findByLibelle(
			final String pLibelle) throws Exception {
		
		/* 
		 * si pLibelle est blank : 
		 * jette une ExceptionAppliLibelleBlank 
		 * avec un message 
		 * MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK. 
		 */
	    if (isBlank(pLibelle)) {
	        throw new ExceptionAppliLibelleBlank(
	        		MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
	    }

	    try {
	    	
	    	/* tente de récupérer une collection d'Entities
			 * JPA par le libellé
			 * dans le stockage via le DAO. */
			/* Recherche insensible à la casse via 
			 * findByProduitIgnoreCase.
	         * Cette méthode est alignée sur 
	         * le contrat implicite du PORT. */
	        final List<ProduitJPA> entities 
	        	= this.produitDaoJPA.findByProduitIgnoreCase(pLibelle);

	        /*
	         * si this.produitDaoJPA
	         * .findByProduitIgnoreCase(pLibelle) retourne null : 
	         *  jette une ExceptionTechniqueGateway 
	         *  avec un message 
	         *  ERREUR_TECHNIQUE_KO_STOCKAGE.
	         */
	        if (entities == null) {
	            throw new ExceptionTechniqueGateway(
	            		ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /* retourne une liste vide si aucune Entity JPA 
	         * n'est trouvée dans le stockage (UC possible). */
	        if (entities.isEmpty()) {
	            return new ArrayList<Produit>();
	        }

	        /* filtre, trie, dédoublonne
			 * et convertit la liste d'Entities. */
			final List<Produit> reponse 
				= this.filtrerTrierDedoublonner(entities);
			
			/* retourne la liste d'objets métier. */
			return reponse;

	    } catch (final ExceptionTechniqueGateway e) {
	    	
	        /* Préserve le message contractuel 
	         * (messages techniques déjà construits). */
	        throw e;
	        
	    } catch (final Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e). */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}
	
	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<Produit> findByLibelleRapide(
			final String pContenu) throws Exception {
		
		/* 
		 * si pContenu est null : 
		 * jette une ExceptionAppliParamNull 
		 * avec un message 
		 * MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL. 
		 */
	    if (pContenu == null) {
	        throw new ExceptionAppliParamNull(
	        		MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
	    }

	    /* Comportement spécifique : si pContenu est blank,
	     * retourne tous les enregistrements (via rechercherTous()).
	     * Cela permet une recherche "tout afficher" 
	     * si l'utilisateur ne saisit rien. */
	    if (isBlank(pContenu)) {
	        return rechercherTous();
	    }

	    try {
	    	
	        /* Délègue au composant technique (DAO) 
	         * la recherche dans le stockage. */
	        final List<ProduitJPA> entities 
	        	= this.produitDaoJPA
	        		.findByProduitContainingIgnoreCase(pContenu);

	        /*
	         * si this.produitDaoJPA
	         * .findByProduitContainingIgnoreCase(pContenu) 
	         * retourne null : 
	         * jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_KO_STOCKAGE.
	         */
	        if (entities == null) {
	            throw new ExceptionTechniqueGateway(
	            		ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /* retourne une liste vide si le contenu n'est pas trouvé. */
	        if (entities.isEmpty()) {
	            return new ArrayList<Produit>();
	        }

	        /* filtre, trie, dédoublonne
			 * et convertit la liste d'Entities. */
			final List<Produit> reponse 
				= this.filtrerTrierDedoublonner(entities);

			/* retourne la liste d'objets métier. */
			return reponse;

	    } catch (final ExceptionTechniqueGateway e) {
	    	
	    	/* Préserve le message contractuel 
	         * (messages techniques déjà construits). */
	        throw e;
	        
	    } catch (final Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e). */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}	
	

	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<Produit> findAllByParent(
			final SousTypeProduit pParent) throws Exception {
		
		/* Recherche tous les Produit enfants d'un parent donné.
	     * Le parent doit être persistant 
	     * (vérifié via verifierPersistanceParent).
	     * Retourne une liste vide si aucun enfant n'est trouvé. */
		
	    /* récupère le parent persisté si il existe. 
	     * ATTENTION : cette méthode peut jeter des Exceptions. */
	    final SousTypeProduitJPA parentPersistant =
	        verifierPersistanceParent(
	        		pParent, PREFIX_MESSAGE_FINDALLBYPARENT);

	    try {
	    	
	        /* délègue la recherche au DAO. */
	        final List<ProduitJPA> entities =
	            this.produitDaoJPA
	            	.findAllBySousTypeProduit(parentPersistant);

	        /*
	         * si this.produitDaoJPA
	         * .findAllBySousTypeProduit() retourne null : 
	         * jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_KO_STOCKAGE.
	         */
	        if (entities == null) {
	            throw new ExceptionTechniqueGateway(
	            		ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /* retourne une liste vide si le contenu n'est pas trouvé. */
	        if (entities.isEmpty()) {
	            return new ArrayList<Produit>();
	        }

			/* filtre, trie, dédoublonne
			 * et convertit la liste d'Entities. */
			final List<Produit> reponse 
				= this.filtrerTrierDedoublonner(entities);

			/* retourne la liste d'objets métier. */
			return reponse;
			
	    } catch (final ExceptionTechniqueGateway e) {
	    	
	    	/* Préserve le message contractuel 
	         * (messages techniques déjà construits). */
	        throw e;
	        
	    } catch (final Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e). */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}
	

	
	/**
	* {@inheritDoc}
	*/
	@Override
	public Produit findById(
			final Long pId) throws Exception {
		
		/* 
		 * si pId est null : 
		 * jette une ExceptionAppliParamNull 
		 * avec un message MESSAGE_FINDBYID_KO_PARAM_NULL. 
		 */
	    if (pId == null) {
	        throw new ExceptionAppliParamNull(
	        		MESSAGE_FINDBYID_KO_PARAM_NULL);
	    }

	    try {

			/* délègue au composant technique (DAO) la recherche. */
			final Optional<ProduitJPA> optEntity 
				= this.produitDaoJPA.findById(pId);

			/* Si le DAO répond null :
			 * jette une ExceptionTechniqueGateway
			 * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (optEntity == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* si le DAO retourne vide, retourne null.*/
			/* Retourne null si l'objet n'est pas trouvé 
			 * (conforme au contrat implicite).
			 * Cela permet de distinguer un objet introuvable 
			 * d'une erreur technique. */
			if (optEntity.isEmpty()) {
				return null;
			}

			/* convertit l'Entity retournée en objet métier. */
			final Produit reponse 
				= ConvertisseurJPAToMetier
					.produitJPAToMetier(optEntity.get());

			/* retourne l'objet métier trouvé.*/
			return reponse;
			
	    } catch (final ExceptionTechniqueGateway e) {

			/* Préserve le message contractuel
			 * (messages techniques déjà construits). */
			throw e;
			
	    } catch (final Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e). */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}
	
	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public Produit update(
			final Produit pObject) throws Exception {
	    
		/* 
		 * si pObject == null : 
		 * jette une ExceptionAppliParamNull 
		 * avec un message 
		 * MESSAGE_UPDATE_KO_PARAM_NULL. 
		 */
	    if (pObject == null) {
	        throw new ExceptionAppliParamNull(
	        		MESSAGE_UPDATE_KO_PARAM_NULL);
	    }
	    
	    /* 
		 * si le libellé est blank : 
		 * jette une ExceptionAppliLibelleBlank 
		 * avec un message 
		 * MESSAGE_UPDATE_KO_LIBELLE_BLANK. 
		 */
	    if (isBlank(pObject.getProduit())) {
	        throw new ExceptionAppliLibelleBlank(
	        		MESSAGE_UPDATE_KO_LIBELLE_BLANK);
	    }

	    final Long id = pObject.getIdProduit();
	    
	    /* 
		 * si l'objet passé en paramètre n'a pas d'ID : 
		 * jette une ExceptionAppliParamNonPersistent 
		 * avec un message 
		 * MESSAGE_UPDATE_KO_NON_PERSISTENT
		 * + pObject.getSousTypeProduit(). 
		 */
	    if (id == null) {
	        throw new ExceptionAppliParamNonPersistent(
	        		MESSAGE_UPDATE_KO_NON_PERSISTENT 
	        		+ safeMessage(pObject.getProduit()));
	    }

	    /* Récupération du parent dans l'objet métier. */
	    final SousTypeProduitI parent = pObject.getSousTypeProduit();
	    
		/* récupère le parent persisté si il existe.*/
		/* ATTENTION : cette méthode peut jeter des Exceptions. */
		final SousTypeProduitJPA parentPersistant 
			= verifierPersistanceParent(parent, PREFIX_MESSAGE_UPDATE);

		try {

			/* Recherche l'Entity JPA persistée
			 * à modifier par ID via le DAO. */
			final Optional<ProduitJPA> optEntity 
				= this.produitDaoJPA.findById(id);

			/* Si le DAO répond null :
			 * jette une ExceptionTechniqueGateway
			 * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (optEntity == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* retourne null si le DAO retourne vide. */
			if (optEntity.isEmpty()) {
				return null;
			}

			final ProduitJPA persiste = optEntity.get();

			/* applique les modifications. */
			/* Optimisation : vérifie si des modifications 
			 * ont été appliquées avant de sauvegarder.
			 * Cela évite une opération de sauvegarde inutile 
			 * si l'objet n'a pas changé. */
			boolean modifie 
				= appliquerModifications(
						persiste, pObject, parentPersistant);

			/* 
			 * Sécurisation contractuelle :
			 * si appliquerModifications(...) ne détecte pas le changement
			 * alors que le contrat exige que l'état réellement persisté
			 * reflète pObject (libellé/parent), on recontrôle explicitement
			 * et on force la modification si nécessaire.
			 */
			if (!modifie) {

				final String nouveauLibelle = pObject.getProduit();
				final String ancienLibelle = persiste.getProduit();

				if (!safeEquals(ancienLibelle, nouveauLibelle)) {
					persiste.setProduit(nouveauLibelle);
					modifie = true;
				}

				final SousTypeProduitI ancienParent = persiste.getSousTypeProduit();
				final SousTypeProduitI nouveauParent = pObject.getSousTypeProduit();

				if (ancienParent == null && nouveauParent == null) {
					/* Pas de changement de parent. */
				} else if (ancienParent == null || nouveauParent == null) {
					persiste.setSousTypeProduit(parentPersistant);
					modifie = true;
				} else if (!safeEquals(ancienParent.getIdSousTypeProduit(),
						nouveauParent.getIdSousTypeProduit())) {
					persiste.setSousTypeProduit(parentPersistant);
					modifie = true;
				}
			}

			/* si il n'y a eu aucune modification :
			 * retourne l'Entity non modifiée convertie en objet metier. */
			if (!modifie) {
				return ConvertisseurJPAToMetier
						.produitJPAToMetier(persiste);
			}

			/* sauvegarde l'Entity JPA modifiée. */
			final ProduitJPA sauvegarde 
				= this.produitDaoJPA.save(persiste);

			/* Si le DAO répond null :
			 * jette une ExceptionTechniqueGateway
			 * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (sauvegarde == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* convertit l'Entity modifiée sauvegardée en objet métier. */
			final Produit reponse 
				= ConvertisseurJPAToMetier
					.produitJPAToMetier(sauvegarde);

			/* retourne l'objet métier modifié. */
			return reponse;

		} catch (final ExceptionTechniqueGateway e) {

			/* Préserve le message contractuel
			 * (messages techniques déjà construits). */
			throw e;

		} catch (final Exception e) {

			final String message 
				= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);

			/* jette une ExceptionTechniqueGateway avec un
			 * message ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e)
			 * et propage l'Exception technique cause. */
			throw new ExceptionTechniqueGateway(message, e);

		}

	} // Fin de update(...).________________________________________________
	
	
	
	/**
	* {@inheritDoc}
	* 
	* <div>
	* <p style="font-weight:bold;">
	* INTENTION TECHNIQUE (scénario nominal) :</p>
	* <ul>
	* <li><span style="font-weight:bold;">Supprimer</span> 
	* une {@link SousTypeProduitJPA}
	* persistée dans le stockage.</li>
	* <li><span style="font-weight:bold;">Garantir</span> 
	* que la suppression est
	* <span style="font-weight:bold;">persistée immédiatement</span>
	* (pas de rollback implicite).</li>
	* <li><span style="font-weight:bold;">
	* Jeter une Exception</span> en cas d'erreur
	* technique (alignement avec le PORT 
	* {@link SousTypeProduitGatewayIService}).</li>
	* </ul>
	* </div>
	*
	* <div>
	* <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	* <ul>
	* <li>delete(null) jette {@link ExceptionAppliParamNull}
	* avec {@link #MESSAGE_DELETE_KO_PARAM_NULL}.</li>
	* <li>delete(id null) jette {@link ExceptionAppliParamNonPersistent}
	* avec {@link #MESSAGE_DELETE_KO_ID_NULL}.</li>
	* <li>delete(absent) ne fait rien (pas d'Exception).</li>
	* <li>delete(nominal) <span style="font-weight:bold;">
	* supprime l'Entity</span>
	* et la rend introuvable.</li>
	* </ul>
	* </div>
	*
	* <div>
	* <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
	* <ul>
	* <li>La suppression est <span style="font-weight:bold;">
	* persistée en base</span>
	* (pas de cache Hibernate masquant les changements).</li>
	* <li>La transaction est <span style="font-weight:bold;">isolée</span>
	* (pas d'interférence avec d'autres opérations).</li>
	* <li>Les Exceptions sont <span style="font-weight:bold;">
	* alignées sur le PORT</span>.</li>
	* </ul>
	* </div>
	*/
	@Override
	public void delete(final Produit pObject) throws Exception {
		
		/* 
	     * si pObject == null : 
	     * jette une ExceptionAppliParamNull 
	     * avec un message 
	     * MESSAGE_DELETE_KO_PARAM_NULL. 
	     */
	    if (pObject == null) {
	        throw new ExceptionAppliParamNull(
	        		MESSAGE_DELETE_KO_PARAM_NULL);
	    }

	    final Long id = pObject.getIdProduit();

	    /* 
	     * si l'ID est null : 
	     * jette une ExceptionAppliParamNonPersistent 
	     * avec un message 
	     * MESSAGE_DELETE_KO_ID_NULL. 
	     */
	    if (id == null) {
	        throw new ExceptionAppliParamNonPersistent(
	        		MESSAGE_DELETE_KO_ID_NULL);
	    }

	    try {
	    	
	        /* Recherche préalable de l'Entity persistée par ID */
	        final Optional<ProduitJPA> optEntity 
	        	= this.produitDaoJPA.findById(id);

	        /* 
	         * Si le DAO retourne null : 
	         * jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_KO_STOCKAGE. 
	         */
	        if (optEntity == null) {
	            throw new ExceptionTechniqueGateway(
	            		ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /* Si l'objet n'existe pas en stockage : return. */
	        if (optEntity.isEmpty()) {
	            return;
	        }

	        /* Suppression immédiate via EntityManager 
	         * pour forcer l'exécution.
	         * Cela évite les problèmes de cache Hibernate 
	         * et garantit que la suppression est persistée en base. */
	        final ProduitJPA entityToDelete = optEntity.get();
	        this.entityManager.remove(entityToDelete);
	        
	        /* Force l'exécution immédiate du DELETE */
	        this.entityManager.flush(); 

	        /* Vérification post-suppression pour confirmer 
	         * que l'objet a bien été supprimé.
	         * Cela permet de détecter les échecs de suppression en base. */
	        final Optional<ProduitJPA> optEntityAfterDelete 
	        	= this.produitDaoJPA.findById(id);
	        
	        if (optEntityAfterDelete.isPresent()) {
	            throw new ExceptionTechniqueGateway(
	                "Échec de la suppression en base pour ID = " + id
	            );
	        }
	        
	    } catch (final ExceptionTechniqueGateway e) {

			/* Préserve le message contractuel
			 * (messages techniques déjà construits). */
			throw e;
			
	    } catch (final Exception e) {
	    	
	        final String message 
	        	= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
	        
	        /* 
	         * jette une ExceptionTechniqueGateway 
	         * avec un message 
	         * ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e) 
	         * et propage l'Exception technique cause. 
	         */
	        throw new ExceptionTechniqueGateway(message, e);
	        
	    }
	}
	

	
	/**
	* {@inheritDoc}
	*/
	@Override
	public long count() throws Exception {

		try {
			
			return this.produitDaoJPA.count();
			
		} catch (final Exception e) {
			
			final String message 
				= ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);
			
			/*
			 * jette une ExceptionTechniqueGateway avec un message
			 * ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e) 
			 * et propage l'Exception technique cause.
			 */
			throw new ExceptionTechniqueGateway(message, e);
			
		}
	}


	// *************************** OUTILS *********************************/
	
	/**
	 * <div>
	 * <p>Setter pour l'EntityManager (nécessaire pour les tests).</p>
	 * </div>
	 *
	 * @param pEntityManager : EntityManager
	 */
	public void setEntityManager(final EntityManager pEntityManager) {
	    this.entityManager = pEntityManager;
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Vérifie que le parent
	 * {@link SousTypeProduit}
	 * est persistant et existe dans le stockage.</p>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li><span style="font-weight:bold;">
	 * Garantit que le parent n'est pas null</span>
	 * (contrat applicatif du PORT).
	 * Si pParent == null :
	 * jette une ExceptionAppliParentNull
	 * avec un message résolu par
	 * {@link #resoudreMessageParentNull(String)}.</li>
	 * <li><span style="font-weight:bold;">
	 * Garantit que le parent n' a pas un libellé blank</span>
	 * (contrat applicatif du PORT).
	 * Si pParent a un libellé blank : jette une
	 * ExceptionAppliLibelleBlank avec un message résolu par
	 * {@link #resoudreMessageLibelleParentBlank(String)}.</li>
	 * <li><span style="font-weight:bold;">
	 * Garantit que le parent a un ID</span> (contrat applicatif du PORT).
	 * Si pParent n'a pas d'ID :
	 * jette une ExceptionTechniqueGatewayNonPersistent
	 * avec un message construit à partir de
	 * {@link #resoudreMessageParentNonPersistent(String)}.</li>
	 * <li>Délègue à un composant technique (DAO)
	 * la tâche de <span style="font-weight:bold;">
	 * vérifier que le parent est persisté dans le stockage</span>.</li>
	 * <li><span style="font-weight:bold;">
	 * Garantit que le parent est persisté dans le stockage</span>.
	 * Si pParent est non persistant :
	 * jette une ExceptionTechniqueGatewayNonPersistent
	 * avec un message construit à partir de
	 * {@link #resoudreMessageParentNonPersistent(String)}.</li>
	 * <li>Si il existe : <span style="font-weight:bold;">
	 * retourne le parent persisté</span>.</li>
	 * <li>Propage une exception technique remontant du stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pParent : SousTypeProduitI :
	 * Parent de l'objet métier dont on veut savoir si il est persistant.
	 * @param pMethode : String :
	 * préfixe d'un message d'une méthode appelante
	 * comme "MESSAGE_CREER" ou "MESSAGE_UPDATE".
	 * @return SousTypeProduitJPA : parent persistant (entity).
	 *
	 * @throws ExceptionAppliParentNull
	 * si pParent null.
	 * @throws ExceptionAppliLibelleBlank
	 * si pParent avec libellé blank.
	 * @throws ExceptionTechniqueGatewayNonPersistent
	 * si pParent sans id ou si pParent absent dans le stockage.
	 * @throws ExceptionTechniqueGateway
	 * si une erreur technique survient lors de l'accès au stockage.
	 * @throws Exception toute autre exception levée par l'implémentation.
	 */
	private SousTypeProduitJPA verifierPersistanceParent(
	        final SousTypeProduitI pParent, final String pMethode)
	                throws Exception {

	    /* Si pParent == null : jette une ExceptionAppliParentNull
	     * avec un message résolu par
	     * resoudreMessageParentNull(String). */
	    /* exemples : MESSAGE_CREER_KO_PARENT_NULL
	     * , MESSAGE_UPDATE_KO_PARENT_NULL.*/
	    if (pParent == null) {
	        throw new ExceptionAppliParentNull(
	                resoudreMessageParentNull(pMethode));
	    }

	    /* Si pParent a un libellé blank :
	     * jette une ExceptionAppliLibelleBlank
	     * avec un message résolu par
	     * resoudreMessageLibelleParentBlank(String). */
	    if (StringUtils.isBlank(pParent.getSousTypeProduit())) {
	        throw new ExceptionAppliLibelleBlank(
	                resoudreMessageLibelleParentBlank(pMethode));
	    }

	    /* Si pParent n'a pas d'ID :
	     * jette une ExceptionTechniqueGatewayNonPersistent
	     * avec un message construit à partir de
	     * resoudreMessageParentNonPersistent(String). */
	    if (pParent.getIdSousTypeProduit() == null) {
	        throw new ExceptionTechniqueGatewayNonPersistent(
	                construireMessageParentNonPersistent(
	                        pMethode, pParent.getSousTypeProduit()));
	    }

	    try {

	        /* Délègue à un composant technique (DAO)
	         * la tâche de vérifier que le parent
	         * est persisté dans le stockage. */
	        final Optional<SousTypeProduitJPA> opt
	            = this.sousTypeProduitDaoJPA.findById(
	                    pParent.getIdSousTypeProduit());

	        if (opt == null || opt.isEmpty()) {
	            /* Si pParent est non persistant :
	             * jette une ExceptionTechniqueGatewayNonPersistent
	             * avec un message construit à partir de
	             * resoudreMessageParentNonPersistent(String). */
	            throw new ExceptionTechniqueGatewayNonPersistent(
	                    construireMessageParentNonPersistent(
	                            pMethode, pParent.getSousTypeProduit()));
	        }

	        final SousTypeProduitJPA reponse = opt.get();

	        /* Si il existe : retourne le parent persisté. */
	        return reponse;

	    } catch (final ExceptionTechniqueGatewayNonPersistent e) {

	        /* Préserve le message contractuel
	         * (messages techniques déjà construits). */
	        throw e;

	    } catch (final Exception e) {

	        final String message
	            = ERREUR_TECHNIQUE_STOCKAGE + safeMessage(e);

	        /* propage une exception technique remontant du stockage
	         * avec un message ERREUR_TECHNIQUE_STOCKAGE
	         * + safeMessage(e) et propage l'Exception cause.*/
	        throw new ExceptionTechniqueGateway(message, e);
	    }
	}


	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Filtre (retire les null), trie, dédoublonne
	 * , et convertit une liste d'entities en liste d'objets métier.</p>
	 * <ul>
	 * <li>retourne une liste vide si pListe == null.</li>
	 * <li>retourne une liste vide si pListe.isEmpty().</li>
	 * <li>alimente la liste réponse en filtrant les null.</li>
	 * <li>trie la liste réponse.</li>
	 * <li>utilise un {@code LinkedHashSet} pour dédoublonner
	 * tout en conservant l'ordre.</li>
	 * <li>convertit chaque Entity de la liste
	 * pListe traitée en objet métier.</li>
	 * <li>retourne la réponse.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pEntities : List&lt;ProduitJPA&gt; :
	 * liste d'Entities à filtrer, trier, dédoublonner et convertir.
	 * @return List&lt;Produit&gt; :
	 * liste d'objets métier. Jamais null.
	 */
	private List<Produit> filtrerTrierDedoublonner(
	        final List<ProduitJPA> pEntities) {

	    /* retourne une liste vide si pListe == null. */
	    if (pEntities == null) {
	        return new ArrayList<Produit>();
	    }

	    /* retourne une liste vide si pListe.isEmpty(). */
	    if (pEntities.isEmpty()) {
	        return new ArrayList<Produit>();
	    }

	    /* Filtre les null et convertit en stream
	     * pour optimiser les opérations. */
	    final List<ProduitJPA> listeNonNull = pEntities.stream()
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());

	    /* Trie la liste des Entities avec le comparateur dédié. */
	    listeNonNull.sort(construireComparateurProduit());

	    /*
	     * Convertit chaque Entity triée en objet métier.
	     */
	    final List<Produit> listeConvertie = listeNonNull.stream()
	            .map(ConvertisseurJPAToMetier::produitJPAToMetier)
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());

	    /*
	     * Dédoublonnage en O(n) basé sur l'égalité Objet Métier
	     * tout en conservant l'ordre grâce à LinkedHashSet.
	     */
	    final Set<Produit> uniques
	    	= new LinkedHashSet<>(listeConvertie);

	    /* Retourne la réponse sous forme de liste. */
	    return new ArrayList<>(uniques);
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Construit et retourne un {@link Comparator} pour trier les
	 * {@link ProduitJPA} selon les règles suivantes :
	 * </p>
	 * <ol>
	 * <li style="font-weight:bold;">
	 * Trie d'abord par le <strong>libellé du SousTypeProduit parent</strong>
	 * (ordre naturel, insensible à la casse).</li>
	 * <li style="font-weight:bold;">
	 * Puis par le <strong>libellé du Produit</strong>
	 * (ordre naturel, insensible à la casse).</li>
	 * </ol>
	 * <p>
	 * Ce comparateur est utilisé pour garantir un ordre cohérent
	 * dans les listes retournées par les méthodes de recherche
	 * (ex: {@link #rechercherTous()}, {@link #findByLibelle(String)}).
	 * </p>
	 * <p>
	 * <span style="font-weight:bold;">Exemple d'ordre :</span>
	 * </p>
	 * <ul>
	 * <li>"vêtement pour enfant" &lt; "vêtement pour femme" &lt; "vêtement pour homme"</li>
	 * <li>Pour un même parent "vêtement pour homme" :
	 * "chemise à manches courtes" &lt; "chemise à manches longues" &lt; "sweat-shirt"</li>
	 * </ul>
	 * </div>
	 *
	 * @return Comparator&lt;ProduitJPA&gt; :
	 * Comparateur prêt à l'emploi pour trier les ProduitJPA.
	 */
	private static Comparator<ProduitJPA> construireComparateurProduit() {
		
	    return (p1, p2) -> {
	        // Cas égaux ou nulls
	        if (p1 == p2) {
	            return 0;
	        }
	        if (p1 == null) {
	            return 1;
	        }
	        if (p2 == null) {
	            return -1;
	        }

	        /* 1. Comparaison par le parent (SousTypeProduit). */
	        final SousTypeProduitI parent1 = p1.getSousTypeProduit();
	        final SousTypeProduitI parent2 = p2.getSousTypeProduit();

	        /* Gestion des parents nulls */
	        if (parent1 == null && parent2 == null) {
	        	
	            /* Pas de parent pour les deux : 
	             * compare les libellés des produits. */
	        } else if (parent1 == null) {
	            return 1; /* p1 sans parent vient après p2. */
	        } else if (parent2 == null) {
	            return -1; /* p1 avec parent vient avant p2. */
	        } else {
	            /* Comparaison des libellés des parents 
	             * (case-insensitive). */
	            final String libelleParent1 
	            = (parent1.getSousTypeProduit() != null)
	                ? parent1.getSousTypeProduit().trim()
	                : "";
	            final String libelleParent2 
	            = (parent2.getSousTypeProduit() != null)
	                ? parent2.getSousTypeProduit().trim()
	                : "";
	            final int comparaisonParents 
	            	= Strings.CI.compare(libelleParent1, libelleParent2);
	            if (comparaisonParents != 0) {
	                return comparaisonParents;
	            }
	        }

	        /* 2. Comparaison des libellés des produits 
	         * (case-insensitive). */
	        final String libelleProduit1 = (p1.getProduit() != null)
	            ? p1.getProduit().trim()
	            : "";
	        final String libelleProduit2 = (p2.getProduit() != null)
	            ? p2.getProduit().trim()
	            : "";
	        
	        return Strings.CI.compare(libelleProduit1, libelleProduit2);
	    };
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Applique les modifications d'un objet métier 
	 * sur une Entity JPA persistée.</p>
	 * <p style="font-weight:bold;">
	 * INTENTION TECHNIQUE (scénario nominal) :</p>
	 * <ul>
	 * <li>Vérifie que l'Entity persistante et l'objet métier 
	 * ne sont pas null.</li>
	 * <li>Compare et met à jour le libellé de l'Entity
	 * si le libellé de l'objet métier est différent.</li>
	 * <li>Compare et met à jour le parent de l'Entity
	 * si le parent de l'objet métier est différent.</li>
	 * <li>Retourne true si au moins une modification a été appliquée.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pPersistant : ProduitJPA :
	 * Entity JPA persistante à modifier.
	 * @param pObject : Produit :
	 * Objet métier portant les modifications.
	 * @param pParentPersistant : SousTypeProduitJPA :
	 * Parent persistant à attacher à l'Entity modifiée.
	 * @return boolean :
	 * true si au moins une modification a été appliquée.
	 */
	private static boolean appliquerModifications(
	        final ProduitJPA pPersistant,
	        final Produit pObject,
	        final SousTypeProduitJPA pParentPersistant) {

	    /* Vérifie que l'Entity persistante et l'objet métier 
	     * ne sont pas null. */
	    if (pPersistant == null || pObject == null) {
	        return false;
	    }

	    boolean modifie = false;

	    /* Compare et met à jour le libellé de l'Entity
	     * si le libellé de l'objet métier est différent. */
	    final String nouveauLibelle = pObject.getProduit();
	    final String ancienLibelle = pPersistant.getProduit();

	    if (!safeEquals(ancienLibelle, nouveauLibelle)) {
	        pPersistant.setProduit(nouveauLibelle);
	        modifie = true;
	    }

	    /* Compare et met à jour le parent de l'Entity
	     * si le parent de l'objet métier est différent. */
	    final SousTypeProduitI ancienParent 
	    	= pPersistant.getSousTypeProduit();
	    final SousTypeProduitI nouveauParent 
	    	= pObject.getSousTypeProduit();

	    if (ancienParent == null && nouveauParent == null) {
	        /* Pas de changement de parent. */
	    } else if (ancienParent == null || nouveauParent == null) {
	        /* L'un des parents est null, donc différent. */
	        pPersistant.setSousTypeProduit(pParentPersistant);
	        modifie = true;
	    } else if (!safeEquals(ancienParent.getIdSousTypeProduit(),
	                          nouveauParent.getIdSousTypeProduit())) {
	        pPersistant.setSousTypeProduit(pParentPersistant);
	        modifie = true;
	    }

	    return modifie;
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit une RequetePage neutre en Pageable Spring.
	 * Construit un {@link Pageable} à partir d'une {@link RequetePage}.
	 * </p>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
	 * <ul>
	 * <li>Utilise des valeurs par défaut si la requête est null.</li>
	 * <li>Extrait le numéro de page et la taille de page de la requête.</li>
	 * <li>Applique un tri par défaut sur le libellé du produit.</li>
	 * <li>Retourne un {@link Pageable} prêt à être utilisé
	 * par Spring Data.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage : RequetePage :
	 * Requête de pagination à convertir.
	 * @return Pageable :
	 * Objet Pageable configuré pour Spring Data.
	 */
	private static Pageable convertirRequetePageEnPageable(
	        final RequetePage pRequetePage) {

	    /* Utilise une requête par défaut si pRequetePage == null. */
	    final RequetePage requete = (pRequetePage != null)
	            ? pRequetePage
	            : new RequetePage();

	    /* Récupère les paramètres de pagination. */
	    final int pageNumber = requete.getPageNumber();
	    final int pageSize = requete.getPageSize();
	    final List<TriSpec> tris = requete.getTris();

	    /* Si pas de tris spécifiés, retourne un Pageable simple. */
	    if ((tris == null) || tris.isEmpty()) {
	        return PageRequest.of(pageNumber, pageSize);
	    }

	    /* Construit le tri à partir des spécifications. */
	    Sort sort = Sort.unsorted();

	    for (final TriSpec tri : tris) {
	        /* Ignore les tris invalides. */
	        if ((tri == null)
	                || (tri.getPropriete() == null)
	                || tri.getPropriete().isBlank()) {
	            continue;
	        }

	        /* Détermine la direction du tri. */
	        final Sort.Direction direction
	        	= DirectionTri.DESC.equals(tri.getDirection())
	                ? Sort.Direction.DESC
	                : Sort.Direction.ASC;

	        /* Ajoute le tri au sort global. */
	        sort = sort.and(Sort.by(direction, tri.getPropriete()));
	    }

	    /* Retourne le Pageable avec tri. */
	    return PageRequest.of(pageNumber, pageSize, sort);
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Résout le message contractuel "parent null"
	 * en fonction de la méthode appelante.</p>
	 * </div>
	 *
	 * @param pMethode : String :
	 * préfixe ("MESSAGE_CREER", "MESSAGE_UPDATE", ...).
	 * @return String :
	 * message contractuel (PORT) si connu, sinon clé technique.
	 */
	private static String resoudreMessageParentNull(
			final String pMethode) {

		if (PREFIX_MESSAGE_CREER.equals(pMethode)) {
			return MESSAGE_CREER_KO_PARENT_NULL;
		}

		if (PREFIX_MESSAGE_FINDBYOBJETMETIER.equals(pMethode)) {
			return MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL;
		}

		if (PREFIX_MESSAGE_FINDALLBYPARENT.equals(pMethode)) {
			return MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL;
		}

		if (PREFIX_MESSAGE_UPDATE.equals(pMethode)) {
			return MESSAGE_UPDATE_KO_PARENT_NULL;
		}

		/* fallback défensif */
		return pMethode + SUFFIXE_KO_PARENT_NULL;
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Résout le message contractuel "libellé parent blank"
	 * en fonction de la méthode appelante.</p>
	 * </div>
	 *
	 * @param pMethode : String : préfixe ("MESSAGE_CREER", "MESSAGE_UPDATE", ...).
	 * @return String : message contractuel (PORT) si connu, sinon clé technique.
	 */
	private static String resoudreMessageLibelleParentBlank(final String pMethode) {

		if (PREFIX_MESSAGE_CREER.equals(pMethode)) {
			return MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK;
		}
		
		if (PREFIX_MESSAGE_FINDBYOBJETMETIER.equals(pMethode)) {
			return MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK;
		}

		if (PREFIX_MESSAGE_FINDALLBYPARENT.equals(pMethode)) {
			return MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK;
		}

		if (PREFIX_MESSAGE_UPDATE.equals(pMethode)) {
			return MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK;
		}

		/* fallback défensif (le PORT ne définit pas partout un message humain). */
		return pMethode + SUFFIXE_KO_LIBELLE_PARENT_BLANK;
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Résout le préfixe contractuel "parent non persistant"
	 * (message humain du PORT) en fonction de la méthode appelante.</p>
	 * <p>
	 * Permet à {@link #verifierPersistanceParent(TypeProduitI, String)}
	 * de rester factorisée tout en produisant des messages alignés
	 * sur le PORT {@link SousTypeProduitGatewayIService}.
	 * </p>
	 * </div>
	 *
	 * @param pMethode : String :
	 * préfixe ("MESSAGE_CREER", "MESSAGE_UPDATE", ...).
	 * @return String :
	 * préfixe contractuel (PORT) si connu, sinon clé technique.
	 */
	private static String resoudreMessageParentNonPersistent(
			final String pMethode) {

		if (PREFIX_MESSAGE_CREER.equals(pMethode)) {
			return MESSAGE_CREER_KO_PARENT_NON_PERSISTENT;
		}

		if (PREFIX_MESSAGE_FINDBYOBJETMETIER.equals(pMethode)) {
			return MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT;
		}

		if (PREFIX_MESSAGE_FINDALLBYPARENT.equals(pMethode)) {
			return MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT;
		}

		if (PREFIX_MESSAGE_UPDATE.equals(pMethode)) {
			return MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT;
		}

		/* fallback défensif */
		return pMethode + SUFFIXE_KO_PARENT_NON_PERSISTENT;
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Construit le message final "parent non persistant"
	 * (préfixe contractuel + libellé du parent).</p>
	 * </div>
	 *
	 * @param pMethode : String :
	 * préfixe ("MESSAGE_CREER", "MESSAGE_UPDATE", ...).
	 * @param pLibelleParent : String :
	 * libellé parent (peut être null).
	 * @return String : message final.
	 */
	private static String construireMessageParentNonPersistent(
			final String pMethode, final String pLibelleParent) {

		final String prefixe = resoudreMessageParentNonPersistent(pMethode);
		return prefixe + safeMessage(pLibelleParent);
	}

	
	
	/**
	 * <div>
	 * <p><b>Détermine si un libellé (ou tout texte fonctionnel) 
	 * est inexploitable du point de vue métier.</b></p>
	 * </div>
	 *
	 * <div>
	 * <p><b>Sens métier :</b></p>
	 * <ul>
	 * <li>Retourne {@code true} si {@code pString} est {@code null} :
	 * absence de valeur fournie (libellé non renseigné).</li>
	 * <li>Retourne {@code true} si {@code pString} 
	 * ne contient que des caractères
	 * d'espacement (blancs) : libellé « vide » et donc non significatif 
	 * pour l'identification
	 * ou la recherche d'un objet métier.</li>
	 * <li>Retourne {@code false} dès que {@code pString} 
	 * contient au moins un caractère
	 * non blanc : valeur considérée comme renseignée et exploitable.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p><b>Remarque technique :</b> utilise {@link String#isBlank()} 
	 * (Java 11+),
	 * qui considère comme blanc tout texte ne comportant 
	 * que des espaces/whitespaces
	 * (sans allocation intermédiaire
	 * , c'est à dire sans créer de nouvelle String intermédiaire).</p>
	 * </div>
	 *
	 * @param pString : String
	 * texte à évaluer (ex : libellé saisi ou contenu de recherche).
	 * @return boolean : {@code true} 
	 * si {@code pString} est {@code null} ou blank,
	 * {@code false} sinon.
	 */
	private static boolean isBlank(final String pString) {
		return pString == null || pString.isBlank();
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Fournit une représentation textuelle sûre d'un objet
	 * sans jamais retourner {@code null}.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p>
	 * Cette méthode est un utilitaire défensif destiné à la
	 * <span style="font-weight:bold;">construction de messages</span>
	 * (notamment messages d'erreur contractuels ou techniques),
	 * afin de garantir qu'aucune concaténation ou propagation
	 * de message ne provoque de {@link NullPointerException}.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">COMPORTEMENT :</p>
	 * <ul>
	 * <li>Si {@code p == null} : retourne une {@link String} vide.</li>
	 * <li>Sinon : invoque {@code p.toString()}.</li>
	 * <li>Si {@code p.toString()} retourne {@code null}
	 * (cas anormal mais défensivement géré) :
	 * retourne une {@link String} vide.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES :</p>
	 * <ul>
	 * <li>Ne retourne <strong>jamais {@code null}</strong>.</li>
	 * <li>Ne lève aucune Exception.</li>
	 * <li>Peut être utilisée en toute sécurité dans
	 * la construction de messages contractuels ou techniques.</li>
	 * </ul>
	 * </div>
	 *
	 * @param p : Object :
	 * objet à convertir en représentation textuelle
	 * (peut être {@code null}).
	 * @return String :
	 * représentation textuelle sûre de {@code p},
	 * jamais {@code null} (chaîne vide si absence de valeur).
	 */
	private static String safeMessage(final Object p) {

		if (p == null) {
			return "";
		}

		final String s = p.toString();
		return (s != null) ? s : "";
	}

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Compare deux objets de manière sûre
	 * sans jamais provoquer de {@link NullPointerException}.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p>
	 * Cette méthode est un utilitaire défensif destiné à effectuer
	 * des comparaisons d'égalité dans un contexte technique
	 * (adapters Gateway, tests d'idempotence, détection de modification),
	 * tout en garantissant une sémantique claire et prévisible
	 * en présence de références {@code null}.
	 * </p>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">COMPORTEMENT :</p>
	 * <ul>
	 * <li>Si {@code p1} et {@code p2} désignent la <strong>même référence</strong>
	 * (y compris les deux à {@code null}) : retourne {@code true}.</li>
	 * <li>Si l'un des deux paramètres est {@code null} et l'autre non :
	 * retourne {@code false}.</li>
	 * <li>Sinon : retourne le résultat de {@code p1.equals(p2)}.</li>
	 * </ul>
	 * </div>
	 *
	 * <div>
	 * <p style="font-weight:bold;">GARANTIES :</p>
	 * <ul>
	 * <li>Ne lève jamais de {@link NullPointerException}.</li>
	 * <li>Ne lève aucune Exception.</li>
	 * <li>Respecte strictement la sémantique de {@link Object#equals(Object)}
	 * lorsque les deux objets sont non {@code null}.</li>
	 * <li>Convient pour des comparaisons fonctionnelles ou techniques
	 * (égalité métier, détection d'absence de modification, etc.).</li>
	 * </ul>
	 * </div>
	 *
	 * @param p1 : Object :
	 * premier objet à comparer (peut être {@code null}).
	 * @param p2 : Object :
	 * second objet à comparer (peut être {@code null}).
	 * @return boolean :
	 * {@code true} si les deux objets sont considérés égaux
	 * selon les règles ci-dessus, {@code false} sinon.
	 */
	private static boolean safeEquals(final Object p1, final Object p2) {

		if (p1 == p2) {
			return true;
		}

		if (p1 == null || p2 == null) {
			return false;
		}

		return p1.equals(p2);
	}

}
