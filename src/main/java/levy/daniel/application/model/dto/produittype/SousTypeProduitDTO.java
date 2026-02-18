package levy.daniel.application.model.dto.produittype;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE SousTypeProduitDTO.java :</p>
 * 
 * <p>DTO pour l'objet métier 
 *  <code style="font-weight:bold;">SousTypeProduit</code>.
 * </p>
 * 
 * <p>
 * Cette classe modélise : 
 * un <span style="font-weight:bold;">DTO</span> pour transférer 
 * les données d'un TypeProduit provenant de la couche 
 * PRESENTATION (VUE ) vers les CONTROLLERS.
 * </p>
 * 
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 17 janvier 2026
 */
public final class SousTypeProduitDTO {

	// ************************ATTRIBUTS************************************/

	/**
	 * Constante null.
	 */
	public static final String NULL = "null";

	/**
	 * ", ".
	 */
	public static final String VIRGULE_ESPACE = ", ";

	/**
	 * "]".
	 */
	public static final String CROCHET_FERMANT = "]";

	/**
	 * ";".
	 */
	public static final String POINT_VIRGULE = ";";
	
	/**
	 * "typeProduit="
	 */
	public static final String TYPEPRODUIT = "typeProduit=";
	
	/**
	 * "sousTypeProduit="
	 */
	public static final String SOUSTYPEPRODUIT = "sousTypeProduit=";

	/**
	 * "sousTypeProduits"
	 */
	public static final String SOUSTYPEPRODUITS = "sousTypeProduits";
	

	/* ----------------------------------------------------------------- */
			
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
		= LogManager.getLogger(SousTypeProduitDTO.class);


	// ***************************METHODES********************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * <p>private pour bloquer l'instanciation (classe utilitaire).</p>
	 * </div>
	 */
	private SousTypeProduitDTO() {
		super();
	}

	

	/* ========================= INNER CLASSES ==========================*/
	
	
	

	/**
	 * <div>
	 * <p>CLASSE INTERNE 
	 * <code style="font-weight:bold;">InputDTO</code> 
	 * pour modéliser les DTO 
	 * qui proviennent des VUES et rentrent dans les CONTROLLERS.</p>
	 * 
	 * <p style="font-weight:bold;">DTO d'ENTREE
	 *  dans la logique métier de l'application (création/modification) 
	 * <code>InputDTO</code>.</p>
	 * 
	 * <p>Ce DTO - ne comportant que des String - provient de la VUE 
	 * de l'Utilisateur, puis du CONTROLLER pour solliciter 
	 * les SERVICES (logique métier).</p>
	 * <p>C'est un CONTENEUR des éléments transmis par la VUE 
	 * de l'Utilisateur à l'application lorsque l'Utilisateur 
	 * demande une création ou une modification d'un Objet métier.</p>
	 * </div>
	 */
	public static final class InputDTO {

		// ********************ATTRIBUTS****************************/

		/**
		 * <div>
		 * <p>Libellé du type de produit parent 
		 * (exemple : "vêtement", "outillage", ...).</p>
		 * </div>
		 */
		private String typeProduit;

		/**
		 * <div>
		 * <p>Libellé du sous-type de produit 
		 * (exemple : "vêtement pour Homme", "vêtement pour femme", ...).</p>
		 * </div>
		 */
		private String sousTypeProduit;
		

		// ********************CONSTRUCTEURS************************/

		/**
		 * <div>
		 * <p>Constructeur par défaut d'arité nulle.</p>
		 * </div>
		 */
		public InputDTO() {
			this(null, null);
		}

		
		
		/**
		 * <div>
		 * <p>Constructeur Complet.</p>
		 * </div>
		 *
		 * @param pTypeProduit : String : type de produit parent.
		 * @param pSousTypeProduit : String : sous-type de produit.
		 */
		public InputDTO(
				final String pTypeProduit,
				final String pSousTypeProduit) {

			super();

			this.typeProduit = pTypeProduit;
			this.sousTypeProduit = pSousTypeProduit;
		}

		
		
		// ********************METHODES*****************************/

		/**
		* {@inheritDoc}
		*/
		@Override
		public int hashCode() {
			return Objects.hash(this.sousTypeProduit, this.typeProduit);
		}

		
		
		/**
		* {@inheritDoc}
		*/
		@Override
		public boolean equals(final Object pObject) {

			if (this == pObject) {
				return true;
			}

			if (pObject == null) {
				return false;
			}

			if (this.getClass() != pObject.getClass()) {
				return false;
			}

			final InputDTO other = (InputDTO) pObject;

			return Objects.equals(this.sousTypeProduit, other.sousTypeProduit)
					&& Objects.equals(this.typeProduit, other.typeProduit);
		}

		
		
		/**
		* {@inheritDoc}
		*/
		@Override
		public String toString() {
			
			final StringBuilder finalBuilder = new StringBuilder();
			
			finalBuilder.append("InputDTO [");
			
			finalBuilder.append(TYPEPRODUIT);
			
			if (this.getTypeProduit() != null) {				
				finalBuilder.append(this.getTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(VIRGULE_ESPACE);
			
			finalBuilder.append(SOUSTYPEPRODUIT);
			
			if (this.getSousTypeProduit() != null) {
				finalBuilder.append(this.getSousTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}
			
			finalBuilder.append(CROCHET_FERMANT);
			
			return finalBuilder.toString();
		}

		
		
		// ********************GETTERS/SETTERS**********************/

		/**
		 * <div>
		 * <p>Getter du Libellé 
		 * du type de produit parent 
		 * (exemple : "Vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @return <code>this.typeProduit</code> : String
		 */
		public String getTypeProduit() {
			return this.typeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Setter du Libellé 
		 * du type de produit parent 
		 * (exemple : "Vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @param pTypeProduit : String : 
		 * valeur à passer à <code>this.typeProduit</code>.
		 */
		public void setTypeProduit(final String pTypeProduit) {
			this.typeProduit = pTypeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Getter du Libellé du sous-type de produit 
		 * (exemple : "vêtement pour Homme", "vêtement pour femme", ...).</p>
		 * </div>
		 *
		 * @return <code>this.sousTypeProduit</code> : String
		 */
		public String getSousTypeProduit() {
			return this.sousTypeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Setter du Libellé du sous-type de produit 
		 * (exemple : "vêtement pour Homme", "vêtement pour femme", ...).</p>
		 * </div>
		 *
		 * @param pSousTypeProduit : String : 
		 * valeur à passer à <code>this.sousTypeProduit</code>
		 */
		public void setSousTypeProduit(final String pSousTypeProduit) {
			this.sousTypeProduit = pSousTypeProduit;
		}

	} // fin InputDTO.-----------------------------------------------------
	
	
	

	/**
	 * <div>
	 * <p>CLASSE INTERNE 
	 * <code style="font-weight:bold;">OutputDTO</code> 
	 * pour modéliser les DTO 
	 * qui, - venant de l'application -, 
	 * proviennent des CONTROLLERS et rentrent dans les VUES.</p>
	 * 
	 * <p style="font-weight:bold;">DTO de SORTIE
	 * (lecture par le CONTROLLER et les VUES)
	 * <code>OutputDTO</code>.</p>
	 * 
	 * <p>Ce DTO - ne comportant que des String + un ID (Long) - 
	 * provient du CONTROLLER (après sollicitation de la logique métier) 
	 * pour alimenter les VUES côté Utilisateur.</p>
	 * 
	 * <p>C'est un CONTENEUR des éléments d'un Objet métier 
	 * transmis par le CONTROLLER 
	 * à la VUE de l'Utilisateur en SORTIE de l'application.</p>
	 * </div>
	 */
	public static final class OutputDTO {

		// ********************ATTRIBUTS****************************/

		/**
		 * <div>
		 * <p>ID technique dans le stockage du sous type de produit.</p>
		 * </div>
		 */
		private Long idSousTypeProduit;

		/**
		 * <div>
		 * <p>Type de produit (libellé) parent 
		 * (exemple : "vêtement", "outillage", ...).</p>
		 * </div>
		 */
		private String typeProduit;

		/**
		 * <div>
		 * <p>Sous-type de produit (libellé).</p>
		 * <p>(exemple : "vêtement pour homme", "vêtement pour femme", ...).</p>
		 * </div>
		 */
		private String sousTypeProduit;

		/**
		 * <div>
		 * <p>Liste&lt;String&gt; des libellés des Produits petits-enfants
		 * qualifiés par le présent SousTypeProduit.</p>
		 * <p>(exemple : "chemise à manches longues pour homme"
		 * , "chemise à manches longues pour femme", ...).</p>
		 * </div>
		 */
		private List<String> produits;

		
		
		// ********************CONSTRUCTEURS************************/

		/**
		 * <div>
		 * <p>Constructeur par défaut d'arité nulle.</p>
		 * </div>
		 */
		public OutputDTO() {
			this(null, null, null, null);
		}

		
		
		/**
		 * <div>
		 * <p>Constructeur Complet.</p>
		 * </div>
		 *
		 * @param pIdSousTypeProduit : Long : 
		 * ID technique dans le stockage du sous type de produit.
		 * @param pTypeProduit: String : Type de produit (libellé) 
		 * parent (exemple : "vêtement", "outillage", ...). 
		 * @param pSousTypeProduit : String : 
		 * Sous-type de produit (libellé). (exemple : 
		 * "vêtement pour homme", "vêtement pour femme", ...).
		 * @param pProduits : Liste&lt;String&gt; : 
		 * Libellés des Produits petits-enfants
		 * qualifiés par le présent SousTypeProduit.
		 */
		public OutputDTO(
				final Long pIdSousTypeProduit,
				final String pTypeProduit,
				final String pSousTypeProduit,
				final List<String> pProduits) {

			super();

			this.idSousTypeProduit = pIdSousTypeProduit;
			this.typeProduit = pTypeProduit;
			this.sousTypeProduit = pSousTypeProduit;
			this.produits = pProduits;
		}

		// ********************METHODES*****************************/

		/**
		* {@inheritDoc}
		*/
		@Override
		public int hashCode() {

			/* ID. */
			final Long id = this.getIdSousTypeProduit();
			
			if (id != null) {
				return Objects.hash(id);
			}
			
			/* Fallback “business key” vers le métier si pas d'ID. */
			/* TypeProduit. */
			final String tp = this.getTypeProduit();
			
			/* SousTypeProduit.  */
			final String stp = this.getSousTypeProduit();
			
			return Objects.hash(stp, tp);
		}

		
		
		/**
		* {@inheritDoc}
		*/
		@Override
		public boolean equals(final Object pObject) {

			if (this == pObject) {
				return true;
			}

			if (pObject == null) {
				return false;
			}

			if (!(pObject instanceof OutputDTO other)) {
				return false;
			}
			
			/* ID. */
			final Long id = this.getIdSousTypeProduit();
			final Long otherId = other.getIdSousTypeProduit();

			if (id != null && otherId != null) {
				return Objects.equals(id, otherId);
			}
			
			/* Fallback “business key” vers le métier si pas d'ID. */
			/* TypeProduit. */
			final String tp = this.getTypeProduit();
			final String otherTp = other.getTypeProduit();

			/* SousTypeProduit.  */
			final String stp = this.getSousTypeProduit();
			final String otherStp = other.getSousTypeProduit();
			
			return Objects.equals(stp, otherStp)
					&& Objects.equals(tp, otherTp);
		}

		
		
		/**
		* {@inheritDoc}
		*/
		@Override
		public String toString() {

			final StringBuilder finalBuilder = new StringBuilder();

			finalBuilder.append("OutputDTO [");

			/* ID. */
			finalBuilder.append("idSousTypeProduit=");

			if (this.getIdSousTypeProduit() != null) {
				finalBuilder.append(this.getIdSousTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(VIRGULE_ESPACE);

			/* TypeProduit. */
			finalBuilder.append(TYPEPRODUIT);

			if (this.getTypeProduit() != null) {
				finalBuilder.append(this.getTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(VIRGULE_ESPACE);
			
			/* SousTypeProduit. */
			finalBuilder.append(SOUSTYPEPRODUIT);
			
			if (this.getSousTypeProduit() != null) {
				finalBuilder.append(this.getSousTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(VIRGULE_ESPACE);
			
			/* produits . */
			finalBuilder.append("produits=");

			if (this.getProduits() != null) {
				finalBuilder.append(this.getProduits().toString());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(CROCHET_FERMANT);

			return finalBuilder.toString();

		}
		

		
		// ********************GETTERS/SETTERS**********************/

		/**
		 * <div>
		 * <p>Getter de l'ID technique dans le stockage 
		 * du sous type de produit.</p>
		 * </div>
		 *
		 * @return <code>this.idSousTypeProduit</code>
		 */
		public Long getIdSousTypeProduit() {
			return this.idSousTypeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Setter de l'ID technique dans le stockage 
		 * du sous type de produit.</p>
		 * </div>
		 *
		 * @param pIdSousTypeProduit : Long : 
		 * valeur à passer à <code>this.idSousTypeProduit</code>
		 */
		public void setIdSousTypeProduit(
				final Long pIdSousTypeProduit) {
			this.idSousTypeProduit = pIdSousTypeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Getter du Type de produit (libellé) parent 
		 * (exemple : "vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @return <code>this.typeProduit</code> : String
		 */
		public String getTypeProduit() {
			return this.typeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Setter du Type de produit (libellé) parent 
		 * (exemple : "vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @param pTypeProduit : String :
		 * valeur à passer à <code>this.typeProduit</code>.
		 */
		public void setTypeProduit(final String pTypeProduit) {
			this.typeProduit = pTypeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Getter du Sous-type de produit (libellé).</p>
		 * <p>(exemple : 
		 * "vêtement pour homme", "vêtement pour femme", ...).</p>
		 * </div>
		 *
		 * @return <code>this.sousTypeProduit</code> : String
		 */
		public String getSousTypeProduit() {
			return this.sousTypeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Setter du Sous-type de produit (libellé).</p>
		 * <p>(exemple : 
		 * "vêtement pour homme", "vêtement pour femme", ...).</p>
		 * </div>
		 *
		 * @param pSousTypeProduit : String : 
		 * valeur à passer à <code>this.sousTypeProduit</code>
		 */
		public void setSousTypeProduit(final String pSousTypeProduit) {
			this.sousTypeProduit = pSousTypeProduit;
		}

		
		
		/**
		 * <div>
		 * <p>Getter de la Liste&lt;String&gt; des 
		 * libellés des Produits petits-enfants
		 * qualifiés par le présent SousTypeProduit.</p>
		 * <p>(exemple : "chemise à manches longues pour homme"
		 * , "chemise à manches longues pour femme", ...).</p>
		 * </div>
		 *
		 * @return <code>this.produits</code> : Liste&lt;String&gt;
		 */
		public List<String> getProduits() {
			return this.produits;
		}

		
		
		/**
		 * <div>
		 * <p>Setter de la Liste&lt;String&gt; des 
		 * libellés des Produits petits-enfants
		 * qualifiés par le présent SousTypeProduit.</p>
		 * <p>(exemple : "chemise à manches longues pour homme"
		 * , "chemise à manches longues pour femme", ...).</p>
		 * </div>
		 *
		 * @param pProduits : Liste&lt;String&gt; : 
		 * valeur à passer à <code>this.produits</code>
		 */
		public void setProduits(final List<String> pProduits) {
			this.produits = pProduits;
		}

	} // fin OutputDTO.----------------------------------------------------

}
