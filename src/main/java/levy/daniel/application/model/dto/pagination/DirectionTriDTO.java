/* ********************************************************************* */
/* ************* enum DirectionTriDTO - DTO **************************** */
/* ********************************************************************* */
package levy.daniel.application.model.dto.pagination;

/**
 * <div>
 * <p style="font-weight:bold;">
 * ENUM DirectionTriDTO.java :
 * </p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette ENUM modélise :
 * <span style="font-weight:bold;">la direction de tri DTO</span>
 * pour la pagination.</li>
 * <li>Cette ENUM est un objet de transport
 * partagé entre les VUES, les CONTROLLERS
 * et les SERVICES UC.</li>
 * <li>Cette ENUM est purement contractuelle
 * et ne dépend d'aucune technologie.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 6 avril 2026
 */
public enum DirectionTriDTO {

	/**
	 * Tri ascendant.
	 */
	ASC,

	/**
	 * Tri descendant.
	 */
	DESC;

}