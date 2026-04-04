package levy.daniel.application.controllers.metier.produittype;

import java.util.List;

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * INTERFACE TypeProduitRechercheIController.java :
 * </p>
 * <p>PORT CONTROLLER</p>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 9 janvier 2026
 */
public interface TypeProduitRechercheIController {
	
	/**
	 * <div>
	 * <p>retourne tous les <code>TypeProduit</code> 
	 * dans le stockage sous forme de DTOs 
	 * <code>TypeProduitDTO.OutputDTO</code>.</p>
	 * </div>
	 *
	 * @return List&lt;TypeProduitDTO.OutputDTO&gt;
	 * @throws Exception 
	 */
	List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception;
	
	
	/**
	 * <div>
	 * <p>retourne tous les <code>TypeProduit</code> 
	 * dans le stockage sous forme de 
	 * <code>String</code>.</p>
	 * </div>
	 *
	 * @return List&lt;String&gt;
	 */
	List<String> rechercherTousString() throws Exception;


}
