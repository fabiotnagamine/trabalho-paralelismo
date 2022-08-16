import utils.RunTimer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.*;

/**
 Este exemplo mostra como usar vários mecanismos Java (incluindo
 a estrutura de fork join pool Java e fluxos sequenciais/paralelos
 framework) para contar o número de arquivos em uma (grande)
 hierarquia de pastas, bem como calcular os tamanhos cumulativos de todos
 os arquivos.
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException {
        System.out.println("Iniciando a contagem dos ficheiros");

        runFileCounterWalkStream();

        // Executa um teste que usa o framework Java fork-join em
        // conjunto com recursos de fluxos paralelos Java.
        runFileCounterParallelStream();

        // Imprimi o tempo de de resposta
        System.out.println(RunTimer.getTimingResults());

        System.out.println("Fim dos contadores");
    }


    private static void runFileCounterWalkStream() throws URISyntaxException {
        runTest(ForkJoinPool.commonPool(),
                new FileCounterWalkStream
                        (new File(ClassLoader.getSystemResource("works").toURI())),
                "fluxo para contar arquivos sem utilizar paralelismo",
                false);
    }


    /**
     * Executa um teste que usa o framework Java fork-join em
     * conjunto com recursos de fluxos paralelos Java.
     */
    private static void runFileCounterParallelStream() throws URISyntaxException {
        runTest(ForkJoinPool.commonPool(),
                new FileCounterParallelStream
                (new File(ClassLoader.getSystemResource("works").toURI())),
                "Contador de Ficheiros utilizando paralelismo",
                true);
    }

    /**
     * Executa todos os testes e imprime os resultados.
     *
     * @param fJPool Usando o fork join pool para executar o teste
     * @param testTask Calcula o tempo de execução
     * @param testName Nome do teste
     */
    private static void runTest(ForkJoinPool fJPool,
                                AbstractFileCounter testTask,
                                String testName,
                                boolean printStats) {
        // Executa o GC primeiro para evitar perturbar os testes.
        System.gc();

        // Executa a tarefa na pasta raiz do diretório.
        long size = RunTimer.timeRun(() -> fJPool.invoke(testTask),
                                     testName);

        // Imprimi os resultados
        System.out.println(testName
                           + ": "
                           + (testTask.documentCount()
                              + testTask.folderCount())
                           + " arquivos ("
                           + testTask.documentCount()
                           + " documentos e  "
                           + testTask.folderCount()
                           + " pastas) contidos: "
                           + size // / 1_000_000)
                           + " bytes");


        if (printStats)
            System.out.println("Pool Size = "
                               + fJPool.getPoolSize()
                               + ", steal count = "
                               + fJPool.getStealCount()
                               + ", running thread count = "
                               + fJPool.getRunningThreadCount());
    }
}
