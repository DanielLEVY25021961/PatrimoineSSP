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
 * (tri ascendant, tri descendant, ...) 
 * utilisée dans les objets de pagination transmis entre les VUES,
 * les CONTROLLERS et les SERVICES UC.</li>
 * <li>Cette ENUM est un objet de transport partagé
 * entre les VUES, les CONTROLLERS et les SERVICES UC,
 * sans logique métier ni dépendance technologique.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 6 avril 2026
 */
public enum DirectionTriDTO {

	/**
	 * <div>
	 * <p>Direction de tri ascendante.</p>
	 * </div>
	 */
	ASC,

	/**
	 * <div>
	 * <p>Direction de tri descendante.</p>
	 * </div>
	 */
	DESC;

}