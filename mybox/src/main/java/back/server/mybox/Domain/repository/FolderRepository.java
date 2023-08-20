package back.server.mybox.Domain.repository;

import back.server.mybox.Domain.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, Long> {
    FolderEntity findByFoldername(String foldername);
    FolderEntity findByFolderId(Long folderId);
}
