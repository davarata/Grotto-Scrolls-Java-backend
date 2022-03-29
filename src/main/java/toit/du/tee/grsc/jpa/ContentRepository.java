package toit.du.tee.grsc.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends CrudRepository<ContentEntity, Long>{

	@Query("SELECT new ContentEntity(c.id, c.title) from ContentEntity c ORDER BY id")
	public List<ContentEntity> getAllContent();

	@Query("SELECT new ContentEntity(c.id, c.title) from ContentEntity c WHERE c.title LIKE %:searchStr%")
	public List<ContentEntity> searchByTitle(@Param("searchStr") String searchStr);
}
