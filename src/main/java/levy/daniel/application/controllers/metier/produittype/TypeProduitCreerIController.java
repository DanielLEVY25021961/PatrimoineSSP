package levy.daniel.application.controllers.metier.produittype;

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * INTERFACE TypeProduitCreerIController.java :
 * </p>
 * <p>PORT CONTROLLER</p>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 janvier 2026
 */
public interface TypeProduitCreerIController {
	
	/**
	 * <div>
	 * <p>stocke un TypeProduit dans le stockage.</p>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO
	 * @return TypeProduitDTO.OutputDTO : 
	 * le DTO retourné par la logique métier
	 * @throws Exception 
	 */
	TypeProduitDTO.OutputDTO creer(TypeProduitDTO.InputDTO pInputDTO) throws Exception;

}
