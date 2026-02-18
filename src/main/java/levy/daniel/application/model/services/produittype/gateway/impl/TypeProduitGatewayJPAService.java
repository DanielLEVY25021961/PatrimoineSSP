/* ********************************************************************* */
/* *********************** ADAPTER GATEWAY ***************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ConvertisseurJPAToMetier;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.ConvertisseurMetierToJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitGatewayJPAService.java :
 * </p>
 * <p style="font-weight:bold;">ADAPTER SERVICE GATEWAY</p>
 *
 * <p>
 * Cette classe modélise :
 * un <span style="font-weight:bold;">SERVICE</span> chargé de :
 * </p>
 * <ul>
 * <li>transformer un <span style="font-weight:bold;">
 * objet métier</span>
 * {@link TypeProduit}
 * en <span style="font-weight:bold;">Entity JPA</span>
 *  {@link TypeProduitJPA}.</li>
 * <li>Manipuler (Sauvegarder, Modifier, ...) l'
 * <span style="font-weight:bold;">Entity JPA</span>
 * au moyen d'un <span style="font-weight:bold;">
 * Repository (DAO) JPA</span>.</li>
 * </ul>
 *
 * <p>
 * ADAPTER GATEWAY JPA implémentant le PORT GATEWAY
 * {@link TypeProduitGatewayIService}.
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
 * @version 1.0
 * @since 28 décembre 2025
 */
@Service("TypeProduitGatewayJPAService")
@Transactional
public class TypeProduitGatewayJPAService
		implements TypeProduitGatewayIService {

	// ************************ATTRIBUTS************************************/

	/**
	 * <div>
	 * <p>Repository (DAO) pour TypeProduitDaoJPA</p>
	 * <p>Injecté via le constructeur par Spring.</p>
	 * </div>
	 */
	private final TypeProduitDaoJPA typeProduitDaoJPA;

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
		= LogManager.getLogger(TypeProduitGatewayJPAService.class);

	// *************************METHODES************************************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * CONSTRUCTEUR appelé automatiquement par SPRING
	 * pour injecter un DAO TypeProduitDaoJPA.</p>
	 * <p>ATTENTION : Ne surtout pas créer de Constructeur d'arité nulle
	 * dans cette classe, faute de quoi SPRING ne pourra plus injecter.</p>
	 * </div>
	 *
	 * @param pTypeProduitDaoJPA : TypeProduitDaoJPA
	 */
	public TypeProduitGatewayJPAService(
			@Qualifier("TypeProduitDaoJPA")
				final TypeProduitDaoJPA pTypeProduitDaoJPA) {
		super();
		this.typeProduitDaoJPA = pTypeProduitDaoJPA;
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeProduit creer(
			final TypeProduit pObject) throws Exception {

		/* si pObject == null : 
		 * jette une ExceptionAppliParamNull 
		 * avec un message 
		 * MESSAGE_CREER_KO_PARAM_NULL.
		 */
		if (pObject == null) {
			throw new ExceptionAppliParamNull(
					MESSAGE_CREER_KO_PARAM_NULL);
		}

		/* si le libellé est blank : 
		 * jette une ExceptionAppliLibelleBlank 
		 * avec un message 
		 * MESSAGE_CREER_KO_LIBELLE_BLANK.
		 */
		if (StringUtils.isBlank(pObject.getTypeProduit())) {
			throw new ExceptionAppliLibelleBlank(
					MESSAGE_CREER_KO_LIBELLE_BLANK);
		}

		try {
			
			/* convertit l'objet métier à stocker en Entity JPA. */
			final TypeProduitJPA entity
				= ConvertisseurMetierToJPA.typeProduitMETIERToJPA(pObject);

			/* délègue au DAO la création dans le stockage. */
			/* récupère l'Entity JPA créée dans le stockage. */
			final TypeProduitJPA sauvegarde
				= this.typeProduitDaoJPA.save(entity);

			/* Si le DAO répond null :
			 * jette une ExceptionTechniqueGateway
			 * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (sauvegarde == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* convertit l'Entity JPA créée en objet métier. */
			final TypeProduit reponse
				= ConvertisseurJPAToMetier
					.typeProduitJPAToMetier(sauvegarde);

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
	public List<TypeProduit> rechercherTous() throws Exception {

	    try {

	        /* récupère la liste complète des Entities auprès du DAO. */
	        final List<TypeProduitJPA> entities
	            = this.typeProduitDaoJPA.findAll();

	        /*
	         * si this.typeProduitDaoJPA.findAll() retourne null : 
	         * jette une ExceptionTechniqueGateway
	         * avec un message ERREUR_TECHNIQUE_KO_STOCKAGE.
	         */
	        if (entities == null) {
	            throw new ExceptionTechniqueGateway(
	                    ERREUR_TECHNIQUE_KO_STOCKAGE);
	        }

	        /*
	         * retourne une liste vide si
	         * this.typeProduitDaoJPA.findAll()
	         * retourne une liste vide.
	         */
	        if (entities.isEmpty()) {
	            return new ArrayList<TypeProduit>();
	        }

	        /* Utilise la méthode dédiée pour 
	         * filtrer, trier, dédoublonner et convertir. */
	        final List<TypeProduit> reponse 
	        	= this.filtrerTrierDedoublonner(entities);
	        
	        /* retourne la reponse. */
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
	public ResultatPage<TypeProduit> rechercherTousParPage(
			final RequetePage pRequetePage) throws Exception {

		/* Choisit la requêtePage par défaut si pRequetePage == null. */
		final RequetePage requete
			= (pRequetePage != null) ? pRequetePage : new RequetePage();

		try {

			/* convertit une RequetePage neutre en Pageable Spring. */
			final Pageable pageable
				= this.convertirRequetePageEnPageable(requete);

			/* récupère la page des Entities auprès du DAO. */
			/* Page Spring -> conversion du contenu + total. */
			final Page<TypeProduitJPA> pageJPA
				= this.typeProduitDaoJPA.findAll(pageable);

			/* si pageJPA == null : jette une
			 * ExceptionTechniqueGateway avec un
			 * message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (pageJPA == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			final List<TypeProduitJPA> contenuJPA
				= pageJPA.getContent();

			/* si contenuJPA == null : jette une
			 * ExceptionTechniqueGateway avec un
			 * message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (contenuJPA == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* convertit la page d'Entities en page d'objets métier. */
			final List<TypeProduit> contenu =
					contenuJPA.stream()
					.filter(e -> e != null)
					.map(ConvertisseurJPAToMetier::typeProduitJPAToMetier)
					.filter(o -> o != null)
					.collect(Collectors.toList());

			/* retourne la page résultat. */
			final ResultatPage<TypeProduit> resultat
				= new ResultatPage<TypeProduit>(
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
	public TypeProduit findByObjetMetier(final TypeProduit pObject)
			throws Exception {

		/* si pObject == null : 
		 * jette une ExceptionAppliParamNull 
		 * avec un message 
		 * MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL. 
		 */
		if (pObject == null) {
			throw new ExceptionAppliParamNull(
					MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
		}

		final String libelle = pObject.getTypeProduit();

		/* si pObject.getTypeProduit() est blank : 
		 * jette une ExceptionAppliLibelleBlank 
		 * avec un message 
		 * MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK.
		 */
		if (libelle == null || libelle.isBlank()) {
			throw new ExceptionAppliLibelleBlank(
					MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
		}

		/* recherche par libellé. */
		/* ATTENTION  gère déjà les Exceptions. */
		final TypeProduit reponse = this.findByLibelle(libelle);

		/* retourne la réponse. */
		return reponse;
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeProduit findByLibelle(
			final String pLibelle) throws Exception {

		/* si pLibelle est blank : 
		 * jette une ExceptionAppliLibelleBlank 
		 * avec un message 
		 * MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK. 
		 */
		if (StringUtils.isBlank(pLibelle)) {
			throw new ExceptionAppliLibelleBlank(
					MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
		}

		try {

			/* tente de récupérer une Entity JPA par son libellé
			 * dans le stockage via le DAO. */
			final TypeProduitJPA entity
				= this.typeProduitDaoJPA
					.findByTypeProduitIgnoreCase(pLibelle);

			/* retourne null si l'Entity JPA n'est pas
			 * trouvée dans le stockage (UC possible). */
			if (entity == null) {
				return null;
			}

			/* convertit l'Entity JPA en Objet métier. */
			final TypeProduit reponse
				= ConvertisseurJPAToMetier
					.typeProduitJPAToMetier(entity);

			/* retourne l'objet métier. */
			return reponse;

		} catch (final Exception e) {

			/* Préserve le message contractuel
			 * (messages techniques déjà construits). */
			if (e instanceof ExceptionTechniqueGateway) {
				throw (ExceptionTechniqueGateway) e;
			}

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
	public List<TypeProduit> findByLibelleRapide(
			final String pContenu) throws Exception {

		/* si pContenu est null : 
		 * jette une ExceptionAppliParamNull 
		 * avec un message 
		 * MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL. 
		 */
		if (pContenu == null) {
			throw new ExceptionAppliParamNull(
					MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
		}

		/* retourne tous les enregistrements si pContenu est blank. */
		if (isBlank(pContenu)) {
			return rechercherTous();
		}

		try {

			/* Délègue au composant technique (DAO)
			 * la recherche dans le stockage. */
			final List<TypeProduitJPA> entities
				= this.typeProduitDaoJPA
					.findByTypeProduitContainingIgnoreCase(pContenu);

			/*
			 * si this.typeProduitDaoJPA.findByTypeProduitContaining(
			 * pContenu) retourne null : 
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
				return new ArrayList<TypeProduit>();
			}

			/* filtre, trie, dédoublonne
			 * et convertit la liste d'Entities. */
			final List<TypeProduit> reponse
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
	public TypeProduit findById(final Long pId) throws Exception {

		/* 
		 * si pId est null : 
		 * jette une ExceptionAppliParamNull 
		 * avec un message 
		 * MESSAGE_FINDBYID_KO_PARAM_NULL. 
		 */
		if (pId == null) {
			throw new ExceptionAppliParamNull(
					MESSAGE_FINDBYID_KO_PARAM_NULL);
		}

		try {

			/* délègue au composant technique (DAO) la recherche. */
			final Optional<TypeProduitJPA> optEntity
				= this.typeProduitDaoJPA.findById(pId);
			
			/* Si le DAO répond null :
			 * jette une ExceptionTechniqueGateway
			 * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (optEntity == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* si le DAO retourne vide, retourne null.*/
			if (optEntity.isEmpty()) {
				return null;
			}

			/* convertit l'Entity retournée en objet métier. */
			final TypeProduit reponse
				= ConvertisseurJPAToMetier
					.typeProduitJPAToMetier(optEntity.get());

			/* retourne l'objet métier trouvé.*/
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
	public TypeProduit update(
			final TypeProduit pObject) throws Exception {

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
		if (StringUtils.isBlank(pObject.getTypeProduit())) {
			throw new ExceptionAppliLibelleBlank(
					MESSAGE_UPDATE_KO_LIBELLE_BLANK);
		}

		final Long id = pObject.getIdTypeProduit();

		/* 
		 * si l'objet passé en paramètre n'a pas d'ID : 
		 * jette une ExceptionAppliParamNonPersistent 
		 * avec un message 
		 * MESSAGE_UPDATE_KO_NON_PERSISTENT
		 *  + pObject.getTypeProduit(). 
		 */
		if (id == null) {
			throw new ExceptionAppliParamNonPersistent(
					MESSAGE_UPDATE_KO_NON_PERSISTENT
					+ pObject.getTypeProduit());
		}

		try {

			/* Recherche l'Entity JPA persistée
			 * à modifier par ID via le DAO. */
			final Optional<TypeProduitJPA> optEntity
				= this.typeProduitDaoJPA.findById(id);
			
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

			final TypeProduitJPA persiste = optEntity.get();

			final boolean modifie
				= appliquerModifications(persiste, pObject);

			/* si il n'y a eu aucune modification :
			 * retourne l'Entity non modifiée convertie en objet metier. */
			if (!modifie) {
				return ConvertisseurJPAToMetier
						.typeProduitJPAToMetier(persiste);
			}

			/* sauvegarde l'Entity JPA modifiée. */
			final TypeProduitJPA sauvegarde
				= this.typeProduitDaoJPA.save(persiste);

			/* Si le DAO répond null :
			 * jette une ExceptionTechniqueGateway
			 * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (sauvegarde == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* convertit l'Entity modifiée sauvegardée en objet métier. */
			final TypeProduit reponse
				= ConvertisseurJPAToMetier
					.typeProduitJPAToMetier(sauvegarde);

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
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(
			final TypeProduit pObject) throws Exception {

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

		final Long id = pObject.getIdTypeProduit();

		/* si l'ID est null : 
		 * jette une ExceptionAppliParamNonPersistent 
		 * avec un message 
		 * MESSAGE_DELETE_KO_ID_NULL. 
		 */
		if (id == null) {
			throw new ExceptionAppliParamNonPersistent(
					MESSAGE_DELETE_KO_ID_NULL);
		}

		try {

			/* Recherche préalable de l'Entity persistée par ID. */
			final Optional<TypeProduitJPA> optEntity
				= this.typeProduitDaoJPA.findById(id);
			
			/* Si le DAO répond null :
			 * jette une ExceptionTechniqueGateway
			 * avec le message ERREUR_TECHNIQUE_KO_STOCKAGE. */
			if (optEntity == null) {
				throw new ExceptionTechniqueGateway(
						ERREUR_TECHNIQUE_KO_STOCKAGE);
			}

			/* Si l'objet n'existe pas en stockage :
			 * ne fait rien. Pas d'Exception. */
			if (optEntity.isEmpty()) {
				return;
			}

			/* détruit l'Entity JPA persistée dans le stockage. */
			this.typeProduitDaoJPA.delete(optEntity.get());
			
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
	public long count() throws Exception {

		try {
			return this.typeProduitDaoJPA.count();
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



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Filtre (retire les null), trie, dédoublonne une liste d'Entities
	 * uniquement sur le libellé (case-insensitive),
	 * et convertit en objets métier.
	 * </p>
	 * <p>
	 * Au sens métier, deux TypeProduit ne peuvent
	 * pas avoir le même libellé.
	 * </p>
	 * <ul>
	 * <li>Si pListe == null : retourne une liste vide.</li>
	 * <li>Si pListe.isEmpty() : retourne une liste vide.</li>
	 * <li>Filtre les nulls.</li>
	 * <li>Trie la liste par libellé (case-insensitive).</li>
	 * <li>Dédoublonne strictement sur le libellé (case-insensitive).</li>
	 * <li>Convertit chaque Entity en objet métier.</li>
	 * <li>Retourne la réponse (jamais {@code null}).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pListe : List&lt;TypeProduitJPA&gt; :
	 * liste d'Entities à filtrer, trier, dédoublonner et convertir.
	 * @return List&lt;TypeProduit&gt; : liste d'objets métier.
	 */
	private List<TypeProduit> filtrerTrierDedoublonner(
	        final List<TypeProduitJPA> pListe) {

	    /* Si pListe == null : retourne une liste vide. */
	    if (pListe == null) {
	        return new ArrayList<TypeProduit>();
	    }

	    /* Si pListe.isEmpty() : retourne une liste vide. */
	    if (pListe.isEmpty()) {
	        return new ArrayList<TypeProduit>();
	    }

	    /*
	     * - Ignore les objets métier dont le libellé est null/blank
	     *   (inexploitables fonctionnellement).
	     * - Dédoublonne sur un libellé normalisé 
	     * (trim + lowerCase(Locale.ROOT))
	     *   et trie ensuite sur la même base normalisée 
	     *   (cohérence dédoublonnage/tri).
	     */

	    /* 1. Filtre les nulls,
	     * convertit en objets métier et dédoublonne en un seul parcours. */
	    final List<TypeProduit> resultat = new ArrayList<TypeProduit>();
	    final Set<String> libellesDejaVus = new HashSet<String>();

	    for (final TypeProduitJPA entity : pListe) {

	        if (entity != null) {

	            final TypeProduit objetMetier =
	                ConvertisseurJPAToMetier.typeProduitJPAToMetier(entity);

	            if (objetMetier != null) {

	                final String libelleBrut = objetMetier.getTypeProduit();

	                /* Ignore les libellés inexploitables (null/blank). */
	                if (libelleBrut == null || libelleBrut.isBlank()) {
	                    continue;
	                }

	                final String libelleNormalise
	                    = libelleBrut.trim().toLowerCase(Locale.ROOT);

	                /* Ignore les libellés ne contenant que des espaces. */
	                if (libelleNormalise.isEmpty()) {
	                    continue;
	                }

	                if (!libellesDejaVus.contains(libelleNormalise)) {
	                    libellesDejaVus.add(libelleNormalise);
	                    resultat.add(objetMetier);
	                }
	            }
	        }
	    }

	    /* 2. Trie la liste finale par libellé normalisé 
	     * (case-insensitive + trim). */
	    Collections.sort(
	        resultat,
	        Comparator.comparing(
	            (TypeProduit tp) -> {

	                if (tp == null) {
	                    return "";
	                }

	                final String lib = tp.getTypeProduit();

	                if (lib == null) {
	                    return "";
	                }

	                return lib.trim().toLowerCase(Locale.ROOT);
	            },
	            String::compareTo
	        )
	    );

	    return resultat;
	}
	

		
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Applique les modifications sur l'Entity persistée 
	 * et retourne true si des modifications 
	 * ont réellement été appliquées.</p>
	 * </div>
	 *
	 * @param pPersistant : TypeProduitJPA : 
	 * Entity persistante à modifier.
	 * @param pObject : TypeProduit : 
	 * objet métier portant des modifications.
	 * @return boolean : true si des modifications ont été appliquées.
	 */
	private static boolean appliquerModifications(
	        final TypeProduitJPA pPersistant,
	        final TypeProduit pObject) {
	
	    /* Si l'objet persistant à modifier ou l'objet
	     * portant les modifications est null : retourne false. */
	    if (pPersistant == null || pObject == null) {
	        return false;
	    }
	
	    final String nouveauLibelle = pObject.getTypeProduit();
	    final String ancienLibelle = pPersistant.getTypeProduit();
	
	    /* modifie les libellés si les libellés
	     * actuels et futurs sont différents. */
	    if (!safeEquals(ancienLibelle, nouveauLibelle)) {
	        pPersistant.setTypeProduit(nouveauLibelle);
	        return true;
	    }
	
	    return false;
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit une requête de pagination métier ({@link RequetePage})
	 * en un objet {@link Pageable} compatible avec Spring Data.
	 * </p>
	 *
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
	 * <ul>
	 *   <li>Gère les cas où {@code pRequetePage} est {@code null}
	 *   en utilisant une requête par défaut.</li>
	 *   <li>Convertit les spécifications de tri ({@link TriSpec})
	 *   en un objet {@link Sort} Spring.</li>
	 *   <li>Filtre les spécifications de tri invalides
	 *   (null, propriété vide ou blank).</li>
	 *   <li>Retourne un {@link Pageable} configuré avec :
	 *     <ul>
	 *       <li>le numéro de page ({@code pageNumber})</li>
	 *       <li>la taille de la page ({@code pageSize})</li>
	 *       <li>les tris valides (si présents)</li>
	 *     </ul>
	 *   </li>
	 *   <li>Si aucune spécification de tri n'est valide,
	 *   retourne un {@link Pageable} non trié.</li>
	 * </ul>
	 *
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 *   <li>Si {@code pRequetePage == null} :
	 *   utilise une nouvelle instance de {@link RequetePage} par défaut.</li>
	 *   <li>Si {@code pRequetePage.getTris() == null} ou vide :
	 *   retourne un {@link Pageable} non trié.</li>
	 *   <li>Si une spécification de tri est invalide
	 *   (null, propriété null/blank) : l'ignore.</li>
	 *   <li>Retourne toujours un objet {@link Pageable} valide
	 *   (jamais {@code null}).</li>
	 * </ul>
	 *
	 * <p style="font-weight:bold;">GARANTIES :</p>
	 * <ul>
	 *   <li>L'objet retourné est toujours compatible avec Spring Data.</li>
	 *   <li>Aucune exception n'est levée en cas de paramètres invalides
	 *   (comportement silencieux).</li>
	 * </ul>
	 *
	 * <p style="font-weight:bold;">EXEMPLES D'UTILISATION :</p>
	 * <ul>
	 *   <li>Si {@code pRequetePage} est {@code null} :
	 *   retourne un {@link Pageable} avec les paramètres par défaut.</li>
	 *   <li>Si {@code pRequetePage.getTris()} est vide :
	 *   retourne un {@link Pageable} non trié.</li>
	 *   <li>Si {@code pRequetePage.getTris()} contient des spécifications invalides :
	 *   les ignore et construit le {@link Pageable} avec les spécifications valides.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pRequetePage : {@link RequetePage} - Requête de pagination métier à convertir.
	 *        Peut être {@code null} (auquel cas une requête par défaut est utilisée).
	 * @return {@link Pageable} - Objet Spring Data configuré pour la pagination et le tri.
	 *         Jamais {@code null}.
	 */
	private Pageable convertirRequetePageEnPageable(
	        final RequetePage pRequetePage) {
	
	    /* Utilise une requête par défaut si pRequetePage est null.
	     * Cela évite les NullPointerException et 
	     * garantit un comportement cohérent. */
	    final RequetePage requete = (pRequetePage != null)
	        ? pRequetePage : new RequetePage();
	    final int pageNumber = requete.getPageNumber();
	    final int pageSize = requete.getPageSize();
	
	    /* Convertit les spécifications de tri en un objet Sort Spring.
	     * 1. Vérifie si la liste des tris est non-null et non-vide.
	     * 2. Pour chaque TriSpec valide (non-null, propriété non-null/non-blank) :
	     *    - Convertit la direction (ASC/DESC) 
	     *    en un objet Sort.Direction Spring.
	     *    - Crée un Sort pour la propriété spécifiée.
	     * 3. Combine les Sort en un seul objet Sort global.
	     * 4. Si aucun tri valide n'est trouvé, retourne un Pageable non trié. */
	    return Optional.ofNullable(requete.getTris())
	        .filter(tris -> !tris.isEmpty())
	        .map(tris -> tris.stream()
	            /* Filtre les TriSpec invalides (null, propriété null/blank).
	             * Cela évite les erreurs lors de la création des objets Sort. */
	            .filter(tri -> tri != null
	                && tri.getPropriete() != null
	                && !tri.getPropriete().isBlank())
	            /* Convertit chaque TriSpec valide en un objet Sort Spring.
	             * La direction est déterminée par la valeur de tri.getDirection(). */
	            .map(tri -> Sort.by(
	                /* Détermine la direction du tri (ASC ou DESC).
	                 * Par défaut, utilise ASC si la direction n'est pas spécifiée. */
	                DirectionTri.DESC.equals(tri.getDirection())
	                    ? Sort.Direction.DESC
	                    : Sort.Direction.ASC,
	                /* Utilise la propriété spécifiée pour le tri.
	                 * La propriété ne peut pas être null ou blank
	                 * (filtrée précédemment). */
	                tri.getPropriete()
	            ))
	            /* Combine tous les Sort en un seul objet Sort global.
	             * Utilise reduce pour accumuler les Sort en un seul objet. */
	            .reduce(Sort.unsorted(), Sort::and)
	        )
	        /* Crée un Pageable avec les paramètres de pagination
	         * et le Sort combiné.
	         * Si des tris valides sont présents, ils sont appliqués. */
	        .map(sort -> PageRequest.of(pageNumber, pageSize, sort))
	        /* Si aucun tri valide n'est trouvé, retourne un Pageable non trié.
	         * Cela garantit toujours un retour valide, même sans tris. */
	        .orElseGet(() -> PageRequest.of(pageNumber, pageSize));
	}



	/**
	 * <div>
	 * <p style="font-weight:bold">
	 * Détermine si un libellé (ou tout texte fonctionnel)
	 * est inexploitable du point de vue métier.</p>
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
	 * (Java 11+).</p>
	 * </div>
	 *
	 * @param pString : String
	 * texte à évaluer.
	 * @return boolean
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
	 * @param p : Object
	 * @return String
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
	 * Compare deux objets de manière sûre.</p>
	 * </div>
	 *
	 * @param p1 : Object
	 * @param p2 : Object
	 * @return boolean
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
