import java.io.File;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Superclasse abstrata para as várias subclasses FileCounter*. */
public abstract class AbstractFileCounter
       extends RecursiveTask<Long> {
    /**
     * O arquivo atual que está sendo analisado.
     */
    protected final File mFile;

    /**
     * Mantém o controle do número total de documentos encontrados.
     */
    protected final AtomicLong mDocumentCount;

    /**
     * Mantém o controle do número total de pastas encontradas.
     */
    protected final AtomicLong mFolderCount;

    /**
     * Constructor initializes the fields.
     */
    AbstractFileCounter(File file) {
        mFile = file;
        mDocumentCount = new AtomicLong(0);
        mFolderCount = new AtomicLong(0);
    }

    
    AbstractFileCounter(File file,
                        AtomicLong documentCount,
                        AtomicLong folderCount) {
        mFile = file;
        mDocumentCount = documentCount;
        mFolderCount = folderCount;
    }

    /**
     * @return O número de documentos contados durante o processo recursivo
     */
    public long documentCount() {
        return mDocumentCount.get();
    }

    /**
     * @return O número de documentos contados durante o processo recursivo
     */
    public long folderCount() {
        return mFolderCount.get();
    }
}

