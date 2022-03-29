package toit.du.tee.grsc.jpa;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
//@Table(name = "content")
public class ContentEntity {

	@Id
	@GeneratedValue()
	private long id;
	private String title;
	@Lob
	@Column(length = 10000)
	private String content;
	private LocalDateTime created;
	private LocalDateTime modified;
	private LocalDateTime accessed;
	// TODO add deletion information.
	// deleteOn is useful, but is it useful to know when deletion was done. What about changing the modified date to the date on which it was deleted?
	// If the contents history is kept, then it would mean that modified could be used to indicate deletion date. But if history is kept and the date on
	// which the changed where made is kept, then modified would not be required in the first place.
	
	protected ContentEntity() { }

	public ContentEntity(
			long id,
			String title,
			String content,
			LocalDateTime created,
			LocalDateTime modified,
			LocalDateTime accessed) {
		this.id = id;
		this.title = title;
		this.content = content;
		
		LocalDateTime now = LocalDateTime.now();
		if (created == null) {
			this.created = now;
		} else {
			this.created = created;
		}
		if (modified == null) {
			this.modified = now;
		} else {
			this.modified = modified;
		}
		if (accessed == null) {
			this.accessed = now;
		} else {
			this.accessed = accessed;
		}
	}

	public ContentEntity(String title, String content) {
		this.title = title;
		this.content = content;
		LocalDateTime now = LocalDateTime.now();
		created = now;
		modified = now;
		accessed = now;
	}

	public ContentEntity(Long id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}

	public LocalDateTime getAccessed() {
		return accessed;
	}

	public void setAccessed(LocalDateTime accessed) {
		this.accessed = accessed;
	}
	
}
