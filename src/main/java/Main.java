import org.apache.commons.math3.distribution.PoissonDistribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        String question = "";
        do {
            System.out.println();
            System.out.println("               CHECK THE MOST LIKELY MATCH RESULT");
            lineDivider();
            List<List<String>> values = new ArrayList<>();

            Scanner scan = new Scanner(System.in);
            String pathName = "C:\\Users\\Larry\\Desktop\\D1Data.csv"; // Full path to csv Data file

            try (Scanner scanner = new Scanner(new File(pathName));) {
                while (scanner.hasNextLine()) {
                    values.add(getValueFromRow(scanner.nextLine()));
                }
            } catch (FileNotFoundException e) {
            }

            System.out.println("** LIST OF TEAMS **");
            List<String> teams = new ArrayList<>();
            for (List i : values) {
                teams.add(i.get(3).toString());
            }
            for (int i = 1; i < getAllTeams(teams).size(); i++) {
                if ((i != 1) && (i - 1) % 6 == 0) System.out.println();
                if (i == getAllTeams(teams).size() - 1) System.out.printf("%-20s\n", teams.get(i));
                else System.out.printf("%-20s", teams.get(i) + ",");
            }

            System.out.println();
            System.out.println("Enter Home Team name:");
            String homeTeam = scan.nextLine();
            System.out.println("Enter Away Team name:");
            String awayTeam = scan.nextLine();
            String homeTeamFullName = "";
            String awayTeamFullName = "";

            for (String name : getAllTeams(teams)) {
                if (name.toLowerCase().contains(homeTeam)) {
                    homeTeamFullName = name;
                }
                if (name.toLowerCase().contains(awayTeam)) {
                    awayTeamFullName = name;
                }
            }
            System.out.println();
            System.out.println(">>> " + homeTeamFullName + " vs " + awayTeamFullName + " <<<");
            System.out.println();

            int totalNumberOfFTHG = values.size() - 1;
            int totalNumberOfFTAG = totalNumberOfFTHG;
            int totalHG = 0;
            int totalAG = 0;
            // total number of Home Goals
            for (int i = 1; i < values.size(); i++) {
                totalHG = totalHG + Integer.parseInt(values.get(i).get(5));
            }
            // total number od Away Goals
            for (int i = 1; i < values.size(); i++) {
                totalAG = totalAG + Integer.parseInt(values.get(i).get(6));
            }


            // variables indispensable for Poisson Distribution
            double homeTeamHG = totalHTGamesPlayed(homeTeam, values);
            double awayTeamAG = totalATGamesPlayed(awayTeam, values);

            double homeTeamGoalsScoredAtHome = totalHTGHome(homeTeam, values);
            double awayTeamGoalsScoredAway = totalHTGAway(awayTeam, values);

            double homeTeamConcededGoalsAtHome = totalHTConcededGoalsHome(homeTeam, values);
            double awayTeamConcededGoalsAway = totalATConcededGoalsAway(awayTeam, values);

            double averageNumberOfGoalsScoredByHomeTeam = (double) totalHG / (double) totalNumberOfFTHG;
            double averageNumberOfGoalsScoredByAwayTeam = (double) totalAG / (double) totalNumberOfFTAG;

            double averageNumberOfConcededGoalsByHomeTeam = (double) totalAG / (double) totalNumberOfFTHG;
            double averageNumberOfConcededGoalsByAwayTeam = (double) totalHG / (double) totalNumberOfFTAG;

            double homeTeamAttackStrength = (homeTeamGoalsScoredAtHome / homeTeamHG) / averageNumberOfGoalsScoredByHomeTeam;
            double homeTeamDefenseStrength = (homeTeamConcededGoalsAtHome / homeTeamHG) / averageNumberOfConcededGoalsByHomeTeam;

            double awayTeamDefenseStrength = (awayTeamConcededGoalsAway / awayTeamAG) / averageNumberOfConcededGoalsByAwayTeam;
            double awayTeamAttackStrength = (awayTeamGoalsScoredAway / awayTeamAG) / averageNumberOfGoalsScoredByAwayTeam;

            double expectedHomeTeamGoals = homeTeamAttackStrength * awayTeamDefenseStrength * averageNumberOfGoalsScoredByHomeTeam;
            double expectedAwayTeamGoals = awayTeamAttackStrength * homeTeamDefenseStrength * averageNumberOfGoalsScoredByAwayTeam;

            System.out.println("Score probability for " + homeTeamFullName);
            lineDivider2();
            for (Map.Entry<Integer, String> entry : (percentageProbabilityOfScoreForEachTeam(expectedHomeTeamGoals)).entrySet()) {
                System.out.println("Scoring " + entry.getKey() + " goals probability is " + entry.getValue());
            }
            System.out.println();
            System.out.println("Score probability for " + awayTeamFullName);
            lineDivider2();
            for (Map.Entry<Integer, String> entry : (percentageProbabilityOfScoreForEachTeam(expectedAwayTeamGoals)).entrySet()) {
                System.out.println("Scoring " + entry.getKey() + " goals probability is " + entry.getValue());
            }

            System.out.println();
            System.out.println("Probability of match result for " + homeTeamFullName + " vs " + awayTeamFullName);
            lineDivider2();
            for (Map.Entry<String, String> entry : (probabilityOfMatchResult(expectedHomeTeamGoals, expectedAwayTeamGoals)).entrySet()) {
                System.out.println("Probability of result " + entry.getKey() + " is " + entry.getValue());
            }

            for (Map.Entry<String, String> entry : (probabilityOfMatchResult(expectedHomeTeamGoals, expectedAwayTeamGoals)).entrySet()) {
                System.out.println("Probability of result " + entry.getKey() + " is " + entry.getValue());
            }
            lineDivider2();
            System.out.print("The most probable result for " + homeTeamFullName + " vs " + awayTeamFullName + " is: ");
            Map.Entry<String, String> entry = null;
            entry = null;
            for (Map.Entry<String, String> entry1 :
                    (probabilityOfMatchResult(expectedHomeTeamGoals, expectedAwayTeamGoals)).entrySet()) {
                if (entry == null || entry1.getValue().compareTo(entry.getValue()) > 0) {
                    entry = entry1;
                    Set<String> max = new HashSet<>();
                    max.add(String.valueOf(entry));
                }
            }
            System.out.println(entry);
            lineDivider();
            System.out.println("Want to try again? Y/N");
            String ask = scan.nextLine();
            question = ask;
        } while (question.equalsIgnoreCase("y"));
    }

    static void lineDivider() {
        System.out.println("----------------------------------------------------------------");
    }
    static void lineDivider2() {
        System.out.println("============================================");

    }

    // take data from each row from file
    private static List<String> getValueFromRow(String row) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScan = new Scanner(row)) {
            rowScan.useDelimiter(",");
            while (rowScan.hasNext()) {
                values.add(rowScan.next());
            }
        }
        return values;
    }

    // take a list of all teams in league
    private static Set<String> getAllTeams(List<String> teams) {
        Set<String> allTeamsSet = new HashSet<>();
        for (String i : teams) {
            allTeamsSet.addAll(teams);
        }
        return allTeamsSet;
    }

    // totalHTGamesPlayed = total number of matches homeTeam played
    static double totalHTGamesPlayed(String teamName, List<List<String>> values) {
        int games = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i).get(3).toLowerCase().contains(teamName)) {
                games++;
            }
        }
        return games;
    }

    // totalATGamesPlayed = total number of matches awayTeam played
    static double totalATGamesPlayed(String teamName, List<List<String>> values) {
        int games = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i).get(4).toLowerCase().contains(teamName)) {
                games++;
            }
        }
        return games;
    }

    // totalHTA = total number of goals scored away by an awayTeam
    static double totalHTGAway(String teamName, List<List<String>> values) {
        double goals = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i).get(4).toLowerCase().contains(teamName)) {
                goals = goals + Integer.parseInt(values.get(i).get(6));
            }
        }
        return goals;
    }

    // totalATConcededGoalsAway - total number of awayTeam conceded goals away
    static double totalATConcededGoalsAway(String teamName, List<List<String>> values) {
        double goalsLost = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i).get(4).toLowerCase().contains(teamName)) {
                goalsLost = goalsLost + Integer.parseInt(values.get(i).get(5));
            }
        }
        return goalsLost;
    }

    // totalHTG = total number of goals scored at home by a homeTeam
    static double totalHTGHome(String teamName, List<List<String>> values) {
        double goals = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i).get(3).toLowerCase().contains(teamName)) {
                goals = goals + Integer.parseInt(values.get(i).get(5));
            }
        }
        return goals;
    }

    // totalHTConcededGoalsHome - total number of homeTeam conceded goals at home
    static double totalHTConcededGoalsHome(String teamName, List<List<String>> values) {
        double goalsLost = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i).get(3).toLowerCase().contains(teamName)) {
                goalsLost = goalsLost + Integer.parseInt(values.get(i).get(6));
            }
        }
        return goalsLost;
    }

    // convert results to percentage
    static String percentageResult(Double probability) {
        int n = (int) (probability * 10000);
        double m = n / 100D;
        return Double.toString(m) + "%";
    }

    // estimated numbers of goals each team can score by using Poisson Distribution
    static Map<Integer, String> percentageProbabilityOfScoreForEachTeam(double teamExpectedGoals) {
        PoissonDistribution distribution = new PoissonDistribution(teamExpectedGoals);
        Map<Integer, String> probability = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            probability.put(i, percentageResult(distribution.probability(i)));
        }
        return probability;
    }

    // calculating a probability match result by using Poisson Distribution
    static Map<String, String> probabilityOfMatchResult(double homeTeamExpectedGoals, double awayTeamExpectedGoals) {
        PoissonDistribution poissonDistributionHomeTeam = new PoissonDistribution(homeTeamExpectedGoals);
        PoissonDistribution poissonDistributionAwayTeam = new PoissonDistribution(awayTeamExpectedGoals);

        Map<Integer, Double> homeTeamProbability = new HashMap<>();
        Map<Integer, Double> awayTeamProbability = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            homeTeamProbability.put(i, poissonDistributionHomeTeam.probability(i));
            awayTeamProbability.put(i, poissonDistributionAwayTeam.probability(i));
        }
        Map<String, String> percentageChanceForEachResult = new TreeMap<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                String goals = i + " - " + j;
                percentageChanceForEachResult.put(goals, percentageResult(homeTeamProbability.get(i) * awayTeamProbability.get(j)));
            }
        }
        return percentageChanceForEachResult;
    }
}
