/* ********************************************************************* */
/* ********************** REPOSITORY DAO JPA *************************** */
/* ********************************************************************* */
package levy.daniel.application.persistence.metier.produittype.dao.daosJPA;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">INTERFACE SousTypeProduitDaoJPA.java :</p>
 *
 * <p>
 * Cette interface modélise :
 * un <span style="font-weight:bold;">Repository JPA</span>
 * pour l'Entity JPA <span style="font-weight:bold;">SousTypeProduitJPA</span>.
 * </p>
 *
 * <p>
 * Elle est utilisée par l'implémentation gateway
 * {@code SousTypeProduitGatewayJPAService} pour :
 * </p>
 * <ul>
 * <li>créer / modifier via {@link #save(SousTypeProduitJPA)}</li>
 * <li>lister toutes les Entities via {@link #findAll()}</li>
 * <li>lister toutes les Entities par pages via 
 * {@link #findAll(Pageable)}</li>
 * <li>rechercher par libellé exact (insensible à la casse) 
 * via {@link #findBySousTypeProduitIgnoreCase(String)}</li>
 * <li>rechercher par libellé contenant (insensible à la casse) 
 * via {@link #findBySousTypeProduitContainingIgnoreCase(String)}</li>
 * <li>rechercher par parent via 
 * {@link #findAllByTypeProduit(TypeProduitJPA)}</li>
 * <li>rechercher par ID via {@link #findById(Long)}</li>
 * <li>compter via {@link #count()}</li>
 * <li>supprimer via {@link #delete(SousTypeProduitJPA)}</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 18 janvier 2026
 */
@Repository("SousTypeProduitDaoJPA")
public interface SousTypeProduitDaoJPA 
		extends JpaRepository<SousTypeProduitJPA, Long> {



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Recherche en base une collection de {@link SousTypeProduitJPA} 
	 * en fonction du libellé exact (insensible à la casse)
	 * contenu dans le paramètre <code>pLibelle</code>.
	 * </p>
	 * <ul>
	 * <li>Retourne une <span style="font-weight:bold;">
	 * liste vide</span> (pas {@code null}) 
	 * si la recherche ne trouve pas.</li>
	 * <li>ATTENTION : le Modèle Conceptuel de Données MCD
	 * et les Entities indiquent que
	 * le libellé n'est
	 * <span style="font-weight:bold;">pas forcément unique</span>
	 * pour un {@link SousTypeProduit}.</li>
	 * <li>C'est le couple [parent, libelle]
	 * qui doit être unique (Cf equals/HashCode
	 * d'un {@link SousTypeProduit}).</li>
	 * <li>Le parent d'un {@link SousTypeProduit} 
	 * est un {@link TypeProduit}.</li>
	 * <li>Cette méthode est utilisée par
	 * {@code SousTypeProduitGatewayJPAService.findBySousTypeProduit(String)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pLibelle : String : 
	 * libellé exact (insensible à la casse) comme "vêtement pour homme".
	 * @return List&lt;SousTypeProduitJPA&gt; : 
	 * la collection d'Entities trouvée, ou liste vide si absente.
	 */
	List<SousTypeProduitJPA> findBySousTypeProduitIgnoreCase(
			String pLibelle);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne la List&lt;SousTypeProduitJPA&gt; 
	 * des {@link SousTypeProduitJPA}
	 * dont le libellé <code>sousTypeProduit</code> contient pContenu 
	 * (insensible à la casse).
	 * </p>
	 * <ul>
	 * <li>Retourne une liste vide si la recherche ne trouve rien
	 * (comportement Spring Data).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pContenu : String : 
	 * contenu partiel (exemple : "homme") insensible à la casse.
	 * @return List&lt;SousTypeProduitJPA&gt; : 
	 * liste des résultats (éventuellement vide),
	 * jamais {@code null}.
	 */
	List<SousTypeProduitJPA> findBySousTypeProduitContainingIgnoreCase(
			String pContenu);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne toutes les {@link SousTypeProduitJPA} du stockage.
	 * </p>
	 * </div>
	 *
	 * @return List&lt;SousTypeProduitJPA&gt; : 
	 * liste complète (éventuellement vide).
	 */
	@Override
	List<SousTypeProduitJPA> findAll();



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne toutes les {@link SousTypeProduitJPA} 
	 * du stockage par pages.
	 * </p>
	 * <ul>
	 * <li>Ne retourne jamais {@code null} en pratique avec Spring Data,
	 * mais le gateway protège tout de même ce cas.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pPageable : Pageable :
	 * objet de pagination.
	 * @return Page&lt;SousTypeProduitJPA&gt; : 
	 * liste complète (éventuellement vide) paginée.
	 */
	@Override
	Page<SousTypeProduitJPA> findAll(Pageable pPageable);



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Retourne la liste des {@link SousTypeProduitJPA} 
	 * rattachés au parent {@link TypeProduitJPA}.
	 * </p>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduitJPA : 
	 * parent (entity).
	 * @return List&lt;SousTypeProduitJPA&gt; : 
	 * résultats (éventuellement vides), jamais {@code null}.
	 */
	List<SousTypeProduitJPA> findAllByTypeProduit(
			TypeProduitJPA pTypeProduit);



}
