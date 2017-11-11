package net.pvytykac.scraper.impl.justice;

import java.util.Date;
import java.util.List;

public class Organization {

	private final String id;
	private final String ico;
	private final String name;
	private final String form;
	private final String address;
	private final Date created;
	private final Date entered;
	private final String purpose;
	private final List<Document> documents;
	private final Integer associations;
	private final Integer capital;

	public Organization(String id, String ico, String name, String form, String address, Date created, Date entered,
			String purpose, List<Document> documents, Integer associations, Integer capital) {
		this.id = id;
		this.ico = ico;
		this.name = name;
		this.form = form;
		this.address = address;
		this.created = created;
		this.entered = entered;
		this.purpose = purpose;
		this.documents = documents;
		this.associations = associations;
		this.capital = capital;
	}

	public String getId() {
		return id;
	}

	public String getIco() {
		return ico;
	}

	public String getName() {
		return name;
	}

	public String getForm() {
		return form;
	}

	public String getAddress() {
		return address;
	}

	public Date getCreated() {
		return created;
	}

	public Date getEntered() {
		return entered;
	}

	public String getPurpose() {
		return purpose;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public Integer getAssociations() {
		return associations;
	}

	public Integer getCapital() {
		return capital;
	}

	@Override
	public String toString() {
		return "Organization{" + "id='" + id + '\'' + ", ico='" + ico + '\'' + ", name='" + name + '\'' + ", form='"
				+ form + '\'' + ", address='" + address + '\'' + ", created=" + created + ", entered=" + entered
				+ ", purpose='" + purpose + '\'' + ", documents=" + documents + ", associations=" + associations
				+ ", capital=" + capital + '}';
	}

	public static class Document {

		private final Integer id;
		private final Integer folderId;
		private final String name;
		private final String type;
		private final Date created;
		private final Date delivered;
		private final Date registered;
		private final Integer pages;
		private final Boolean digitalized;

		public Document(Integer id, Integer folderId, String name, String type, Date created, Date delivered,
				Date registered, Integer pages, Boolean digitalized) {
			this.id = id;
			this.folderId = folderId;
			this.name = name;
			this.type = type;
			this.created = created;
			this.delivered = delivered;
			this.registered = registered;
			this.pages = pages;
			this.digitalized = digitalized;
		}

		public Integer getId() {
			return id;
		}

		public Integer getFolderId() {
			return folderId;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public Date getCreated() {
			return created;
		}

		public Date getDelivered() {
			return delivered;
		}

		public Date getRegistered() {
			return registered;
		}

		public Integer getPages() {
			return pages;
		}

		public Boolean getDigitalized() {
			return digitalized;
		}

		@Override
		public String toString() {
			return "Document{" + "id=" + id + ", folderId=" + folderId + ", name='" + name + '\'' + ", type='" + type
					+ '\'' + ", created=" + created + ", delivered=" + delivered + ", registered=" + registered
					+ ", pages=" + pages + ", digitalized=" + digitalized + '}';
		}
	}



}
