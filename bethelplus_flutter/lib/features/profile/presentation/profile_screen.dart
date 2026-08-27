import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../../../data/providers/bethel_providers.dart';

class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final profile = ref.watch(userProfileProvider);
    final stats = ref.watch(spiritualStatsProvider);
    final profileNotifier = ref.read(userProfileProvider.notifier);

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: const Text(
          "Profil & Statistiques",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 12.0),
        children: [
          // 1. User Avatar & Identity Card
          Card(
            elevation: 1,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
            color: colorScheme.surface,
            child: Padding(
              padding: const EdgeInsets.all(18.0),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 32,
                    backgroundColor: colorScheme.primaryContainer,
                    child: Text(
                      profile.firstName.isNotEmpty ? profile.firstName[0].toUpperCase() : "F",
                      style: TextStyle(
                        fontSize: 26,
                        fontWeight: FontWeight.bold,
                        color: colorScheme.onPrimaryContainer,
                      ),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          profile.firstName.isNotEmpty ? profile.firstName : "Fidèle de Béthel",
                          style: theme.textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: colorScheme.onSurface,
                          ),
                        ),
                        Text(
                          profile.email.isNotEmpty ? profile.email : "Compte Local Anonyme",
                          style: theme.textTheme.bodySmall?.copyWith(
                            color: colorScheme.onSurfaceVariant,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          "Objectif quotidien : ${profile.dailyGoalMinutes} min",
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: colorScheme.primary,
                          ),
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.edit_outlined),
                    onPressed: () => _openEditProfileDialog(context, ref, profile.firstName),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // 2. Spiritual Stats Counters
          Row(
            children: [
              Expanded(
                child: _buildStatCard(
                  context,
                  title: "Série Active",
                  value: "${stats.currentStreak} jours",
                  icon: Icons.local_fire_department_rounded,
                  iconColor: Colors.orange,
                  colorScheme: colorScheme,
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: _buildStatCard(
                  context,
                  title: "Temps Total",
                  value: "${stats.totalMinutes} min",
                  icon: Icons.hourglass_top_rounded,
                  iconColor: Colors.blue,
                  colorScheme: colorScheme,
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: _buildStatCard(
                  context,
                  title: "Sessions",
                  value: "${stats.totalSessions}",
                  icon: Icons.self_improvement_rounded,
                  iconColor: Colors.purple,
                  colorScheme: colorScheme,
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),

          // 3. Weekly Activity Heatmap
          Card(
            elevation: 1,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
            color: colorScheme.surface,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        "Activité des 7 derniers jours",
                        style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
                      ),
                      Text(
                        "${stats.totalSessions} sessions",
                        style: theme.textTheme.bodySmall?.copyWith(color: colorScheme.primary, fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                  const SizedBox(height: 14),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: stats.weeklyHeatmap.map((day) {
                      final hasActivity = day.sessionCount > 0;
                      return Column(
                        children: [
                          Container(
                            width: 38,
                            height: 38,
                            decoration: BoxDecoration(
                              color: hasActivity
                                  ? colorScheme.primary
                                  : colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
                              shape: BoxShape.circle,
                              border: day.isToday
                                  ? Border.all(color: colorScheme.primary, width: 2)
                                  : null,
                            ),
                            child: Center(
                              child: Text(
                                "${day.dayOfMonth}",
                                style: TextStyle(
                                  fontSize: 13,
                                  fontWeight: FontWeight.bold,
                                  color: hasActivity
                                      ? Colors.white
                                      : colorScheme.onSurfaceVariant,
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(height: 6),
                          Text(
                            day.dayName,
                            style: TextStyle(
                              fontSize: 11,
                              fontWeight: day.isToday ? FontWeight.bold : FontWeight.normal,
                              color: day.isToday ? colorScheme.primary : colorScheme.onSurfaceVariant,
                            ),
                          ),
                        ],
                      );
                    }).toList(),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // 4. Spiritual Badges Wall
          Card(
            elevation: 1,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
            color: colorScheme.surface,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    "Badges & Récompenses Spirituelles",
                    style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 12),
                  ...stats.unlockedBadges.map((badge) {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 10.0),
                      child: Row(
                        children: [
                          Container(
                            width: 42,
                            height: 42,
                            decoration: BoxDecoration(
                              color: badge.isUnlocked
                                  ? Colors.amber.withValues(alpha: 0.2)
                                  : colorScheme.surfaceContainerHighest,
                              shape: BoxShape.circle,
                            ),
                            child: Icon(
                              badge.isUnlocked ? Icons.military_tech_rounded : Icons.lock_outline_rounded,
                              color: badge.isUnlocked ? Colors.amber.shade700 : Colors.grey,
                            ),
                          ),
                          const SizedBox(width: 14),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  badge.title,
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    color: badge.isUnlocked ? colorScheme.onSurface : Colors.grey,
                                  ),
                                ),
                                Text(
                                  badge.subtitle,
                                  style: TextStyle(
                                    fontSize: 12,
                                    color: badge.isUnlocked ? colorScheme.onSurfaceVariant : Colors.grey,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          if (badge.isUnlocked)
                            const Icon(Icons.check_circle_rounded, color: Colors.green, size: 18),
                        ],
                      ),
                    );
                  }),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // 5. App Settings & Preferences
          Card(
            elevation: 1,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
            color: colorScheme.surface,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    "Paramètres de l'Application",
                    style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 12),

                  // Theme Selector
                  ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: const Icon(Icons.brightness_6_rounded),
                    title: const Text("Thème visuel"),
                    subtitle: Text(
                      profile.themeMode == "DARK"
                          ? "Nuit (Sombre)"
                          : profile.themeMode == "LIGHT"
                              ? "Sable (Clair)"
                              : "Automatique (Système)",
                    ),
                    trailing: DropdownButton<String>(
                      value: profile.themeMode,
                      underline: const SizedBox(),
                      items: const [
                        DropdownMenuItem(value: "DARK", child: Text("Nuit")),
                        DropdownMenuItem(value: "LIGHT", child: Text("Sable")),
                        DropdownMenuItem(value: "SYSTEM", child: Text("Système")),
                      ],
                      onChanged: (val) {
                        if (val != null) {
                          profileNotifier.setThemeMode(val);
                        }
                      },
                    ),
                  ),
                  const Divider(),

                  // Daily Prayer Goal Slider
                  ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: const Icon(Icons.track_changes_rounded),
                    title: const Text("Objectif quotidien de prière"),
                    subtitle: Text("${profile.dailyGoalMinutes} minutes par jour"),
                  ),
                  Slider(
                    value: profile.dailyGoalMinutes.toDouble(),
                    min: 5,
                    max: 60,
                    divisions: 11,
                    label: "${profile.dailyGoalMinutes} min",
                    onChanged: (val) {
                      profileNotifier.setDailyGoalMinutes(val.toInt());
                    },
                  ),
                  const Divider(),

                  // Auto DND Toggle
                  SwitchListTile(
                    contentPadding: EdgeInsets.zero,
                    secondary: const Icon(Icons.do_not_disturb_on_rounded),
                    title: const Text("Mode Silence pendant la prière"),
                    subtitle: const Text("Coupe les distractions lors du recueillement"),
                    value: profile.isAutoDndEnabled,
                    onChanged: (val) {
                      profileNotifier.toggleAutoDnd(val);
                    },
                  ),
                  const Divider(),

                  // iCloud & Cloud Backup Section Header
                  const ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: Icon(Icons.cloud_sync_rounded, color: Colors.blue),
                    title: Text("Sauvegarde & Synchronisation"),
                    subtitle: Text("iCloud (iOS) et Cloud Multi-Appareils"),
                  ),
                  const SizedBox(height: 8),

                  Consumer(
                    builder: (context, ref, _) {
                      final cloudState = ref.watch(cloudSyncProvider);
                      final cloudNotifier = ref.read(cloudSyncProvider.notifier);

                      return Container(
                        padding: const EdgeInsets.all(14),
                        decoration: BoxDecoration(
                          color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
                          borderRadius: BorderRadius.circular(14),
                          border: Border.all(color: colorScheme.outlineVariant.withValues(alpha: 0.4)),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // iCloud Section
                            Row(
                              children: [
                                const Icon(Icons.apple, size: 20),
                                const SizedBox(width: 8),
                                const Text("iCloud Backup (iOS)", style: TextStyle(fontWeight: FontWeight.bold)),
                                const Spacer(),
                                if (cloudState.isICloudSyncing)
                                  const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2))
                              ],
                            ),
                            const SizedBox(height: 4),
                            Text(
                              cloudState.lastICloudBackupTime != null
                                  ? "Dernière sauvegarde iCloud : ${DateFormat('dd/MM/yyyy HH:mm').format(cloudState.lastICloudBackupTime!)}"
                                  : "Aucune sauvegarde iCloud locale trouvée",
                              style: TextStyle(fontSize: 12, color: colorScheme.onSurfaceVariant),
                            ),
                            const SizedBox(height: 10),
                            Row(
                              children: [
                                Expanded(
                                  child: OutlinedButton.icon(
                                    onPressed: cloudState.isICloudSyncing
                                        ? null
                                        : () async {
                                            await cloudNotifier.backupToICloud();
                                            if (context.mounted) {
                                              ScaffoldMessenger.of(context).showSnackBar(
                                                SnackBar(content: Text(cloudState.message ?? "Sauvegarde iCloud terminée")),
                                              );
                                            }
                                          },
                                    icon: const Icon(Icons.cloud_upload_outlined, size: 16),
                                    label: const Text("Sauvegarder", style: TextStyle(fontSize: 12)),
                                  ),
                                ),
                                const SizedBox(width: 8),
                                Expanded(
                                  child: OutlinedButton.icon(
                                    onPressed: cloudState.isICloudSyncing
                                        ? null
                                        : () async {
                                            await cloudNotifier.restoreFromICloud();
                                            if (context.mounted) {
                                              ScaffoldMessenger.of(context).showSnackBar(
                                                SnackBar(content: Text(cloudState.message ?? "Restauration effectuée")),
                                              );
                                            }
                                          },
                                    icon: const Icon(Icons.cloud_download_outlined, size: 16),
                                    label: const Text("Restaurer", style: TextStyle(fontSize: 12)),
                                  ),
                                ),
                              ],
                            ),

                            const Divider(height: 24),

                            // Supabase Cloud Section
                            Row(
                              children: [
                                const Icon(Icons.sync_rounded, size: 20, color: Colors.teal),
                                const SizedBox(width: 8),
                                const Text("Supabase Cloud Sync", style: TextStyle(fontWeight: FontWeight.bold)),
                                const Spacer(),
                                if (cloudState.isSyncing)
                                  const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2))
                              ],
                            ),
                            const SizedBox(height: 4),
                            Text(
                              cloudState.lastSyncTime != null
                                  ? "Dernière synchro : ${DateFormat('dd/MM/yyyy HH:mm').format(cloudState.lastSyncTime!)}"
                                  : "Synchronisation automatique multi-plateformes",
                              style: TextStyle(fontSize: 12, color: colorScheme.onSurfaceVariant),
                            ),
                            const SizedBox(height: 10),
                            SizedBox(
                              width: double.infinity,
                              child: FilledButton.tonalIcon(
                                onPressed: cloudState.isSyncing
                                    ? null
                                    : () async {
                                        await cloudNotifier.syncSupabase();
                                        if (context.mounted) {
                                          ScaffoldMessenger.of(context).showSnackBar(
                                            SnackBar(content: Text(cloudState.message ?? "Synchronisation effectuée")),
                                          );
                                        }
                                      },
                                icon: const Icon(Icons.bolt, size: 18),
                                label: const Text("Synchroniser avec le Cloud"),
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 80),
        ],
      ),
    );
  }

  Widget _buildStatCard(
    BuildContext context, {
    required String title,
    required String value,
    required IconData icon,
    required Color iconColor,
    required ColorScheme colorScheme,
  }) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
      ),
      color: colorScheme.surface,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 14),
        child: Column(
          children: [
            Icon(icon, color: iconColor, size: 24),
            const SizedBox(height: 6),
            Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
            ),
            Text(
              title,
              style: TextStyle(fontSize: 11, color: colorScheme.onSurfaceVariant),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  void _openEditProfileDialog(BuildContext context, WidgetRef ref, String currentName) {
    final controller = TextEditingController(text: currentName);
    showDialog(
      context: context,
      builder: (ctx) {
        return AlertDialog(
          title: const Text("Modifier mon prénom"),
          content: TextField(
            controller: controller,
            decoration: const InputDecoration(
              hintText: "Votre prénom...",
              border: OutlineInputBorder(),
            ),
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Annuler")),
            FilledButton(
              onPressed: () {
                final newName = controller.text.trim();
                if (newName.isNotEmpty) {
                  final current = ref.read(userProfileProvider);
                  ref.read(userProfileProvider.notifier).updateProfile(
                    current.copyWith(firstName: newName),
                  );
                }
                Navigator.pop(ctx);
              },
              child: const Text("Enregistrer"),
            ),
          ],
        );
      },
    );
  }
}
