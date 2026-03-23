package levy.daniel.application.model.dto.produittype;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitDTO.java :</p>
 * 
 * <p>DTO pour l'objet métier 
 * <code style="font-weight:bold;">Produit</code>.
 * </p>
 * 
 * <p>
 * Cette classe modélise : 
 * un <span style="font-weight:bold;">DTO</span> pour transférer 
 * les données d'un Produit provenant de la couche 
 * PRESENTATION (VUE ) vers les CONTROLLERS.
 * </p>
 * 
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 17 janvier 2026
 */
public final class ProduitDTO {

	// ************************ATTRIBUTS************************************/

	/**
	 * "null"
	 */
	public static final String NULL = "null";

	/**
	 * ", "
	 */
	public static final String VIRGULE_ESPACE = ", ";

	/**
	 * ']'
	 */
	public static final char CROCHET_FERMANT = ']';

	/**
	 * ';'
	 */
	public static final char POINT_VIRGULE = ';';

	/**
	 * "typeProduit="
	 */
	public static final String TYPEPRODUIT = "typeProduit=";

	/**
	 * "sousTypeProduit="
	 */
	public static final String SOUSTYPEPRODUIT = "sousTypeProduit=";

	/**
	 * "produit="
	 */
	public static final String PRODUIT = "produit=";


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
	private static final Logger LOG = LogManager
			.getLogger(ProduitDTO.class);

	// ***************************METHODES********************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * <p>private pour bloquer l'instanciation (classe utilitaire).</p>
	 * </div>
	 */
	private ProduitDTO() {
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
		 * (exemple : "Homme", "Femme", "Canne", ...).</p>
		 * </div>
		 */
		private String sousTypeProduit;

		/**
		 * <div>
		 * <p>Libellé du produit.</p>
		 * <p>(exemple : "chemise à manches longues", ...).</p>
		 * </div>
		 */
		private String produit;


		// ********************CONSTRUCTEURS************************/

		/**
		 * <div>
		 * <p>Constructeur par défaut d'arité nulle.</p>
		 * </div>
		 */
		public InputDTO() {
			this(null, null, null);
		}


		/**
		 * <div>
		 * <p>Constructeur Complet.</p>
		 * </div>
		 *
		 * @param pTypeProduit : String : type de produit parent.
		 * @param pSousTypeProduit : String : sous-type de produit.
		 * @param pProduit : String : Libellé du produit.
		 */
		public InputDTO(
				final String pTypeProduit,
				final String pSousTypeProduit,
				final String pProduit) {

			super();
			this.typeProduit = pTypeProduit;
			this.sousTypeProduit = pSousTypeProduit;
			this.produit = pProduit;
		}


		// ********************METHODES*****************************/

		/**
		* {@inheritDoc}
		*/
		@Override
		public int hashCode() {
			return Objects.hash(this.getProduit()
					, this.getSousTypeProduit()
						, this.getTypeProduit());
		}


		
		/**
		* {@inheritDoc}
		*/
		@Override
		public boolean equals(final Object pObject) {

			if (this == pObject) {
				return true;
			}

			if (!(pObject instanceof InputDTO other)) {
				return false;
			}

			return Objects.equals(this.getProduit(), other.getProduit())
					&& Objects.equals(this.getSousTypeProduit()
							, other.getSousTypeProduit())
					&& Objects.equals(this.getTypeProduit()
							, other.getTypeProduit());
		}


		
		/**
		* {@inheritDoc}
		*/
		@Override
		public String toString() {

			final StringBuilder finalBuilder = new StringBuilder();

			finalBuilder.append("InputDTO [");

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

			/* Produit. */
			finalBuilder.append(PRODUIT);

			if (this.getProduit() != null) {
				finalBuilder.append(this.getProduit());
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
		 * (exemple : "Homme", "Femme", "Canne", ...).</p>
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
		 * (exemple : "Homme", "Femme", "Canne", ...).</p>
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
		 * <p>Getter du Libellé du produit.</p>
		 * </div>
		 *
		 * @return <code>this.produit</code> : String
		 */
		public String getProduit() {
			return this.produit;
		}


		
		/**
		 * <div>
		 * <p>Setter du Libellé du produit.</p>
		 * </div>
		 *
		 * @param pProduit : String : 
		 * valeur à passer à <code>this.produit</code>
		 */
		public void setProduit(final String pProduit) {
			this.produit = pProduit;
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
		 * <p>ID technique dans le stockage du produit.</p>
		 * </div>
		 */
		private Long idProduit;

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
		 * <p>(exemple : "vêtement Homme", "vêtement Femme",  ...).</p>
		 * </div>
		 */
		private String sousTypeProduit;

		/**
		 * <div>
		 * <p>Libellé du Produit 
		 * (exemple "chemise à manches longues pour homme").</p>
		 * </div>
		 */
		private String produit;


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
		 * @param pIdProduit : Long : 
		 * id technique dans le stockage du produit.
		 * @param pTypeProduit : String : type produit parent.
		 * @param pSousTypeProduit : String : Libellé du sous-type produit.
		 * @param pProduit : String : Libellé du Produit.
		 */
		public OutputDTO(
				final Long pIdProduit,
				final String pTypeProduit,
				final String pSousTypeProduit,
				final String pProduit) {

			super();
			this.idProduit = pIdProduit;
			this.typeProduit = pTypeProduit;
			this.sousTypeProduit = pSousTypeProduit;
			this.produit = pProduit;
		}


		// ********************METHODES*****************************/

		/**
		* {@inheritDoc}
		*/
		@Override
		public int hashCode() {

			/* ID. */
			final Long id = this.getIdProduit();

			if (id != null) {
				return Objects.hash(id);
			}

			/* Fallback “business key” vers le métier si pas d'ID. */
			final String p = this.getProduit();
			final String stp = this.getSousTypeProduit();
			final String tp = this.getTypeProduit();

			return Objects.hash(p, stp, tp);
		}


		/**
		* {@inheritDoc}
		*/
		@Override
		public boolean equals(final Object pObject) {

			if (this == pObject) {
				return true;
			}

			if (!(pObject instanceof OutputDTO other)) {
				return false;
			}

			/* ID. */
			final Long id = this.getIdProduit();
			final Long otherId = other.getIdProduit();

			if ((id != null) && (otherId != null)) {
				return Objects.equals(id, otherId);
			}

			/* Fallback “business key” vers le métier si pas d'ID. */
			return Objects.equals(this.getProduit(), other.getProduit())
					&& Objects.equals(this.getSousTypeProduit()
							, other.getSousTypeProduit())
					&& Objects.equals(this.getTypeProduit()
							, other.getTypeProduit());
		}


		/**
		* {@inheritDoc}
		*/
		@Override
		public String toString() {

			final StringBuilder finalBuilder = new StringBuilder();

			finalBuilder.append("OutputDTO [");

			/* ID. */
			finalBuilder.append("idProduit=");

			if (this.getIdProduit() != null) {
				finalBuilder.append(this.getIdProduit());
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

			/* Produit. */
			finalBuilder.append(PRODUIT);

			if (this.getProduit() != null) {
				finalBuilder.append(this.getProduit());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(CROCHET_FERMANT);

			return finalBuilder.toString();
		}


		// ********************GETTERS/SETTERS**********************/

		/**
		 * <div>
		 * <p>Getter de l'ID technique dans le stockage du produit.</p>
		 * </div>
		 *
		 * @return <code>this.idProduit</code> : Long
		 */
		public Long getIdProduit() {
			return this.idProduit;
		}


		/**
		 * <div>
		 * <p>Setter de l'ID technique dans le stockage du produit.</p>
		 * </div>
		 *
		 * @param pIdProduit : Long :
		 * valeur à passer à <code>this.idProduit</code>.
		 */
		public void setIdProduit(final Long pIdProduit) {
			this.idProduit = pIdProduit;
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
		 * <p>(exemple : "vêtement Homme", "vêtement Femme",  ...).</p>
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
		 * <p>(exemple : "vêtement Homme", "vêtement Femme",  ...).</p>
		 * </div>
		 *
		 * @param pSousTypeProduit : String :
		 * valeur à passer à <code>this.sousTypeProduit</code>.
		 */
		public void setSousTypeProduit(final String pSousTypeProduit) {
			this.sousTypeProduit = pSousTypeProduit;
		}


		/**
		 * <div>
		 * <p>Getter du Produit (libellé)
		 * (exemple "chemise à manches longues pour homme").</p>
		 * </div>
		 *
		 * @return <code>this.produit</code> : String
		 */
		public String getProduit() {
			return this.produit;
		}


		/**
		 * <div>
		 * <p>Setter du Produit (libellé)
		 * (exemple "chemise à manches longues pour homme").</p>
		 * </div>
		 *
		 * @param pProduit : String :
		 * valeur à passer à <code>this.produit</code>.
		 */
		public void setProduit(final String pProduit) {
			this.produit = pProduit;
		}

	} // fin OutputDTO.----------------------------------------------------

}
