import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../data/models/prayer_reminder.dart';
import '../../../data/models/spiritual_alarm.dart';
import '../../../data/providers/bethel_providers.dart';
import '../../../data/services/notification_service.dart';

class RemindersScreen extends ConsumerWidget {
  const RemindersScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final reminders = ref.watch(remindersProvider);
    final remindersNotifier = ref.read(remindersProvider.notifier);
    final alarm = ref.watch(spiritualAlarmProvider);
    final alarmNotifier = ref.read(spiritualAlarmProvider.notifier);

    final canonicalReminders = reminders.where((r) => r.isCanonical).toList();
    final customReminders = reminders.where((r) => !r.isCanonical).toList();

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: const Text(
          "Heures Saintes & Rappels",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.add_alarm_rounded),
            tooltip: "Ajouter un rappel",
            onPressed: () => _openAddReminderDialog(context, ref),
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 12.0),
        children: [
          // 1. Spiritual Alarm (Réveil Spirituel) Card
          Card(
            elevation: 2,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
              side: BorderSide(color: colorScheme.primary.withValues(alpha: 0.5), width: 1.5),
            ),
            color: colorScheme.surface,
            child: Padding(
              padding: const EdgeInsets.all(18.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Row(
                        children: [
                          Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(
                              color: colorScheme.primaryContainer,
                              shape: BoxShape.circle,
                            ),
                            child: Icon(Icons.wb_sunny_rounded, color: colorScheme.onPrimaryContainer, size: 20),
                          ),
                          const SizedBox(width: 12),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                "Réveil Spirituel",
                                style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                              ),
                              Text(
                                "${alarm.hour.toString().padLeft(2, '0')}:${alarm.minute.toString().padLeft(2, '0')}",
                                style: TextStyle(
                                  fontSize: 22,
                                  fontWeight: FontWeight.bold,
                                  color: colorScheme.primary,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                      Switch(
                        value: alarm.isEnabled,
                        onChanged: (val) => alarmNotifier.toggleAlarm(val),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.4),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          "Parole du réveil :",
                          style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: colorScheme.primary),
                        ),
                        const SizedBox(height: 2),
                        Text(
                          "« ${alarm.scriptureText} »",
                          style: const TextStyle(fontSize: 13, fontStyle: FontStyle.italic),
                        ),
                        Text(
                          "— ${alarm.scriptureReference}",
                          style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: colorScheme.primary),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      TextButton.icon(
                        onPressed: () => _openAlarmSettingsDialog(context, ref, alarm),
                        icon: const Icon(Icons.edit_outlined, size: 16),
                        label: const Text("Régler l'heure"),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 20),

          // 2. Canonical 4 Prayer Hours
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                "Les 4 Heures Saintes Canoniques",
                style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
              ),
              TextButton(
                onPressed: () => remindersNotifier.restoreCanonicalReminders(),
                child: const Text("Réinitialiser", style: TextStyle(fontSize: 12)),
              ),
            ],
          ),
          const SizedBox(height: 8),

          ...canonicalReminders.map((reminder) {
            final timeStr = "${reminder.hour.toString().padLeft(2, '0')}:${reminder.minute.toString().padLeft(2, '0')}";
            return Card(
              elevation: 0,
              margin: const EdgeInsets.only(bottom: 10),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
              ),
              color: colorScheme.surface,
              child: ListTile(
                leading: Container(
                  width: 44,
                  height: 44,
                  decoration: BoxDecoration(
                    color: reminder.isEnabled
                        ? colorScheme.primaryContainer
                        : colorScheme.surfaceContainerHighest,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Center(
                    child: Text(
                      timeStr,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 12,
                        color: reminder.isEnabled
                            ? colorScheme.onPrimaryContainer
                            : colorScheme.onSurfaceVariant,
                      ),
                    ),
                  ),
                ),
                title: Text(
                  reminder.title,
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    color: reminder.isEnabled ? colorScheme.onSurface : Colors.grey,
                  ),
                ),
                subtitle: Text("Son : ${reminder.soundType}", style: const TextStyle(fontSize: 12)),
                trailing: Switch(
                  value: reminder.isEnabled,
                  onChanged: (val) => remindersNotifier.toggleReminder(reminder.id, val),
                ),
              ),
            );
          }),
          const SizedBox(height: 16),

          // 3. Custom Reminders Section
          if (customReminders.isNotEmpty) ...[
            Text(
              "Rappels Personnalisés",
              style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            ...customReminders.map((reminder) {
              final timeStr = "${reminder.hour.toString().padLeft(2, '0')}:${reminder.minute.toString().padLeft(2, '0')}";
              return Card(
                elevation: 0,
                margin: const EdgeInsets.only(bottom: 10),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                  side: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
                ),
                color: colorScheme.surface,
                child: ListTile(
                  leading: Text(
                    timeStr,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                  ),
                  title: Text(reminder.title, style: const TextStyle(fontWeight: FontWeight.bold)),
                  trailing: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Switch(
                        value: reminder.isEnabled,
                        onChanged: (val) => remindersNotifier.toggleReminder(reminder.id, val),
                      ),
                      IconButton(
                        icon: const Icon(Icons.delete_outline_rounded, size: 20, color: Colors.grey),
                        onPressed: () => remindersNotifier.deleteReminder(reminder.id),
                      ),
                    ],
                  ),
                ),
              );
            }),
          ],

          const SizedBox(height: 20),

          // 4. Test Notification Button
          OutlinedButton.icon(
            onPressed: () async {
              final messenger = ScaffoldMessenger.of(context);
              await NotificationService.instance.showInstantNotification(
                id: 999,
                title: "Béthel+ — Heure Sainte",
                body: "« C'est ici la maison de Dieu, la porte des cieux. » Entrez dans le silence de la prière.",
              );
              messenger.showSnackBar(
                const SnackBar(content: Text("Notification test envoyée !")),
              );
            },
            icon: const Icon(Icons.notifications_active_rounded),
            label: const Text("Tester une notification instantanée"),
            style: OutlinedButton.styleFrom(
              minimumSize: const Size(double.infinity, 48),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
            ),
          ),
          const SizedBox(height: 80),
        ],
      ),
    );
  }

  void _openAddReminderDialog(BuildContext context, WidgetRef ref) {
    final titleController = TextEditingController(text: "Mon temps de recueillement");
    TimeOfDay selectedTime = const TimeOfDay(hour: 7, minute: 0);

    showDialog(
      context: context,
      builder: (ctx) {
        return StatefulBuilder(
          builder: (ctx, setDialogState) {
            return AlertDialog(
              title: const Text("Nouveau rappel de prière"),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(
                    controller: titleController,
                    decoration: const InputDecoration(
                      labelText: "Titre du rappel",
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 16),
                  ListTile(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                      side: BorderSide(color: Colors.grey.shade400),
                    ),
                    leading: const Icon(Icons.access_time_rounded),
                    title: Text(
                      "${selectedTime.hour.toString().padLeft(2, '0')}:${selectedTime.minute.toString().padLeft(2, '0')}",
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                    ),
                    subtitle: const Text("Touchez pour modifier l'heure"),
                    onTap: () async {
                      final time = await showTimePicker(
                        context: context,
                        initialTime: selectedTime,
                      );
                      if (time != null) {
                        setDialogState(() {
                          selectedTime = time;
                        });
                      }
                    },
                  ),
                ],
              ),
              actions: [
                TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Annuler")),
                FilledButton(
                  onPressed: () {
                    final newReminder = PrayerReminder(
                      id: DateTime.now().millisecondsSinceEpoch % 100000,
                      title: titleController.text.trim(),
                      hour: selectedTime.hour,
                      minute: selectedTime.minute,
                      isEnabled: true,
                      soundscape: "HARPE",
                      canonicalType: 'CUSTOM',
                    );
                    ref.read(remindersProvider.notifier).saveReminder(newReminder);
                    Navigator.pop(ctx);
                  },
                  child: const Text("Ajouter"),
                ),
              ],
            );
          },
        );
      },
    );
  }

  void _openAlarmSettingsDialog(BuildContext context, WidgetRef ref, SpiritualAlarm alarm) {
    TimeOfDay selectedTime = TimeOfDay(hour: alarm.hour, minute: alarm.minute);

    showDialog(
      context: context,
      builder: (ctx) {
        return StatefulBuilder(
          builder: (ctx, setDialogState) {
            return AlertDialog(
              title: const Text("Régler le Réveil Spirituel"),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  ListTile(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                      side: BorderSide(color: Colors.grey.shade400),
                    ),
                    leading: const Icon(Icons.alarm_rounded),
                    title: Text(
                      "${selectedTime.hour.toString().padLeft(2, '0')}:${selectedTime.minute.toString().padLeft(2, '0')}",
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
                    ),
                    subtitle: const Text("Modifier l'heure du réveil"),
                    onTap: () async {
                      final time = await showTimePicker(context: context, initialTime: selectedTime);
                      if (time != null) {
                        setDialogState(() {
                          selectedTime = time;
                        });
                      }
                    },
                  ),
                ],
              ),
              actions: [
                TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Annuler")),
                FilledButton(
                  onPressed: () {
                    ref.read(spiritualAlarmProvider.notifier).saveAlarm(
                      alarm.copyWith(hour: selectedTime.hour, minute: selectedTime.minute),
                    );
                    Navigator.pop(ctx);
                  },
                  child: const Text("Enregistrer"),
                ),
              ],
            );
          },
        );
      },
    );
  }
}
