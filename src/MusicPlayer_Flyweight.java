import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class MusicPlayer_Flyweight {
    public static void main(String[] args) {
        // Console demo showing album reuse
        AlbumFactory.clear();
        Album a1 = AlbumFactory.getAlbum("Revenge of the Fallen", "Linkin Park", 300_000);
        Album a2 = AlbumFactory.getAlbum("Imagine Dragons with JID", "Imagine Dragons", 300_000);
        Album a3 = AlbumFactory.getAlbum("Encore (2004)", "Eminem", 450_000);

        Song s1 = new Song("New Divide", a1, 12.3, "Revenge of the Fallen");
        Song s2 = new Song("Enemy", a2, 0.0, "Imagine Dragons with JID");
        Song s3 = new Song("Mocking Bird", a3, 45.0, "Encore (2004)");

        System.out.println("s1.album == s2.album ? " + (s1.getAlbum() == s2.getAlbum())); // true
        System.out.println("s1.album == s3.album ? " + (s1.getAlbum() == s3.getAlbum())); // false
        System.out.println("Number of unique Album flyweights: " + AlbumFactory.getAlbumCount()); // 2

        // Launch GUI
        SwingUtilities.invokeLater(() -> new MusicPlayerFrame().setVisible(true));
    }
}

/* -------------------- Flyweight: Album -------------------- */
class Album {
    private final String albumName;
    private final String artist;
    private final byte[] artwork; // simulated heavy resource (image bytes)

    public Album(String albumName, String artist, int artworkSizeBytes) {
        this.albumName = albumName;
        this.artist = artist;
        this.artwork = new byte[artworkSizeBytes];
        Arrays.fill(this.artwork, (byte) 1); // simulate occupied memory
    }

    public String getAlbumName() { return albumName; }
    public String getArtist() { return artist; }
    public int getArtworkSize() { return artwork.length; }
}

/* -------------------- Flyweight Factory -------------------- */
class AlbumFactory {
    private static final Map<String, Album> pool = new HashMap<>();

    private static String key(String name, String artist) {
        return (name + "||" + artist).toUpperCase(Locale.ROOT);
    }

    public static Album getAlbum(String name, String artist, int artworkSizeBytes) {
        String k = key(name, artist);
        return pool.computeIfAbsent(k, kk -> new Album(name, artist, artworkSizeBytes));
    }

    public static int getAlbumCount() { return pool.size(); }

    public static Collection<Album> getAllAlbums() { return Collections.unmodifiableCollection(pool.values()); }

    public static void clear() { pool.clear(); }
}

/* -------------------- Extrinsic Song objects -------------------- */
class Song {
    private final String title;
    private final double playbackPosition; // seconds
    private final String playlistName;
    private final Album album; // shared flyweight reference

    public Song(String title, Album album, double playbackPosition, String playlistName) {
        this.title = title;
        this.album = album;
        this.playbackPosition = playbackPosition;
        this.playlistName = playlistName;
    }

    public String getTitle() { return title; }
    public double getPlaybackPosition() { return playbackPosition; }

    public String getPlaylistName() { return playlistName; }
    public Album getAlbum() { return album; }
}

/* -------------------- GUI: Musical themed player showing sharing stats -------------------- */
class MusicPlayerFrame extends JFrame {
    private final DefaultListModel<String> songListModel = new DefaultListModel<>();
    private final java.util.List<Song> songs = new ArrayList<>();
    private final JTextArea statsArea = new JTextArea(6, 30);

    // simulated artwork size per album (bytes)
    private final int simulatedArtworkPerAlbum = 300_000;

    public MusicPlayerFrame() {
        setTitle("🎵 MelodyShare — Flyweight Music Player Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(new HeaderPanel(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(12,12,12,12));

        // Left: song list
        JList<String> songList = new JList<>(songListModel);
        songList.setFont(new Font("Inter", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(songList);
        sp.setBorder(BorderFactory.createTitledBorder("Now Playing (instances)"));
        center.add(sp, BorderLayout.CENTER);

        // Right: controls + stats
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Controls & Stats"));

        JPanel addPanel = new JPanel(new GridLayout(5,2,6,6));
        JTextField titleField = new JTextField("New Divide");
        JTextField albumField = new JTextField("Revenge of the Fallen");
        JTextField artistField = new JTextField("Linkin Park");
        JTextField playlistField = new JTextField("Badass Vibes");
        JButton addBtn = new JButton("Add Song (reuse album)");

        addPanel.add(new JLabel("Title:")); addPanel.add(titleField);
        addPanel.add(new JLabel("Album:")); addPanel.add(albumField);
        addPanel.add(new JLabel("Artist:")); addPanel.add(artistField);
        addPanel.add(new JLabel("Playlist:")); addPanel.add(playlistField);
        addPanel.add(new JLabel()); addPanel.add(addBtn);

        right.add(addPanel);
        right.add(Box.createRigidArea(new Dimension(0,10)));

        JButton addMany = new JButton("Add 1000 Songs (random)");
        JButton clear = new JButton("Clear Songs");
        right.add(addMany);
        right.add(Box.createRigidArea(new Dimension(0,6)));
        right.add(clear);
        right.add(Box.createRigidArea(new Dimension(0,12)));

        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        right.add(new JScrollPane(statsArea));

        center.add(right, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);

        // actions
        addBtn.addActionListener(e -> {
            String t = titleField.getText().trim(); if (t.isEmpty()) t = "Unknown";
            String alb = albumField.getText().trim(); if (alb.isEmpty()) alb = "Unknown Album";
            String art = artistField.getText().trim(); if (art.isEmpty()) art = "Unknown Artist";
            String pl = playlistField.getText().trim(); if (pl.isEmpty()) pl = "Default";

            Album a = AlbumFactory.getAlbum(alb, art, simulatedArtworkPerAlbum);
            Song s = new Song(t, a, 0.0, pl);
            songs.add(s);
            songListModel.addElement(formatSongEntry(s));
            refreshStats();
        });

        addMany.addActionListener(e -> addRandomSongs());
        clear.addActionListener(e -> {
            songs.clear(); songListModel.clear(); AlbumFactory.clear(); refreshStats();
        });

        refreshStats();
    }

    private String formatSongEntry(Song s) {
        return String.format("%s — %s [Album: %s | Artist: %s] (pos: %.1fs)",
                s.getTitle(), s.getPlaylistName(), s.getAlbum().getAlbumName(), s.getAlbum().getArtist(), s.getPlaybackPosition());
    }

    private void addRandomSongs() {
        Random rnd = new Random();
        String[] sampleTitles = {"Sunbeam","Late Night","Dreamscape","City Lights","Echoes","Waves"};
        String[] albums = {"Blue Skies","Night Beats","Oceanic","Retro Pop","Deep Space"};
        String[] artists = {"Sunny Band","Moon Trio","Wave Riders","Vintage 5","AstroFlow"};

        for (int i = 0; i < 1000; i++) {
            int ai = rnd.nextInt(albums.length);
            String alb = albums[ai];
            String art = artists[ai];
            String title = sampleTitles[rnd.nextInt(sampleTitles.length)] + " " + (rnd.nextInt(999)+1);
            String pl = "AutoGen" + (ai+1);
            Album a = AlbumFactory.getAlbum(alb, art, simulatedArtworkPerAlbum + ai*50_000);
            Song s = new Song(title, a, rnd.nextDouble()*300.0, pl);
            songs.add(s);
            if (i < 200) songListModel.addElement(formatSongEntry(s)); // only show first 200 to keep UI responsive
        }
        songListModel.addElement("... (showing first 200 of " + 1000 + " newly added songs)");
        refreshStats();
    }

    private void refreshStats() {
        SwingUtilities.invokeLater(() -> {
            int songCount = songs.size();
            int albumCount = AlbumFactory.getAlbumCount();
            long artworkPerAlbum = simulatedArtworkPerAlbum;
            long totalIfSeparate = artworkPerAlbum * (long) songCount;
            long totalWithFlyweight = artworkPerAlbum * (long) albumCount;
            long saved = Math.max(0, totalIfSeparate - totalWithFlyweight);

            String sb = String.format("Songs (instances): %d\n", songCount) +
                    String.format("Unique Albums (flyweights): %d\n", albumCount) +
                    String.format("Artwork per album (simulated): %d bytes (~%.2f KB)\n", artworkPerAlbum, artworkPerAlbum / 1024.0) +
                    String.format("If each song had its own artwork: %.2f MB\n", totalIfSeparate / 1024.0 / 1024.0) +
                    String.format("With flyweights (shared): %.2f MB\n", totalWithFlyweight / 1024.0 / 1024.0) +
                    String.format("Estimated memory saved: %.2f MB\n", saved / 1024.0 / 1024.0);
            statsArea.setText(sb);
        });
    }
}

class HeaderPanel extends JPanel {
    public HeaderPanel() { setPreferredSize(new Dimension(100, 110)); }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0,0,new Color(120,15,90), w, h, new Color(230,90,160));
        g2.setPaint(gp);
        g2.fillRect(0,0,w,h);

        g2.setColor(Color.white);
        g2.setFont(new Font("Poppins", Font.BOLD, 24));
        g2.drawString("MelodyShare — Flyweight Music Player", 18, 36);

        g2.setFont(new Font("Inter", Font.PLAIN, 13));
        g2.drawString("Share album data across songs to save memory — beautiful demo UI.", 18, 58);

        // musical doodles
        g2.setFont(new Font("Serif", Font.BOLD, 48));
        g2.drawString("♬", w - 90, 60);
        g2.drawString("♪", w - 50, 90);
    }
}